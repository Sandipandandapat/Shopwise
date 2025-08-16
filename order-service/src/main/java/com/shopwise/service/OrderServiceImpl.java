package com.shopwise.service;

import com.shopwise.controller.InventoryServiceClient;
import com.shopwise.controller.ProductServiceClient;
import com.shopwise.dto.OrderItemReqDto;
import com.shopwise.dto.OrderReqDto;
import com.shopwise.dto.OrderResponseDto;
import com.shopwise.dto.ProductResponseDto;
import com.shopwise.entity.OrderItems;
import com.shopwise.entity.OrderStatus;
import com.shopwise.entity.Order;
import com.shopwise.event.OrderEvent;
import com.shopwise.event.OrderItemEvent;
import com.shopwise.kafka.OrderEventProducer;
import com.shopwise.mapper.OrderMapper;
import com.shopwise.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderEventProducer orderEventProducer;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper mapper,
                            ProductServiceClient productServiceClient, InventoryServiceClient inventoryServiceClient,
                            OrderEventProducer orderEventProducer){
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderReqDto orderRequest) {
        List<OrderItems> orderItems = processOrder(orderRequest.getOrderItems());
        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(orderItems)
                .totalAmount(totalAmount)
                .build();
        orderItems.forEach(item->item.setOrder(order));
        Order savedOrder = orderRepository.save(order);

        orderEventProducer.sendOrderEvent(
                new OrderEvent(
                        savedOrder.getOrderId(),
                        savedOrder.getUserId(),
                        savedOrder.getOrderItems().stream()
                                .map(item -> new OrderItemEvent(item.getProductId(),item.getQuantity()))
                                .toList(),
                        savedOrder.getTotalAmount()
                )
        );
        return mapper.toOrderResponseDto(savedOrder);
    }


    private List<OrderItems> processOrder(List<OrderItemReqDto> orderItems) {
        return orderItems.stream()
                .map(this::processOrderItems)
                .toList();
    }

    private OrderItems processOrderItems(OrderItemReqDto itemReq){
        checkInventoryAvailability(itemReq);
        ProductResponseDto product = getProduct(itemReq.getProductId());
        BigDecimal itemTotal = calculateItemTotal(itemReq.getQuantity(),product.getPrice());

        return OrderItems.builder()
                .productId(itemReq.getProductId())
                .quantity(itemReq.getQuantity())
                .price(itemTotal)
                .build();
    }

    private ProductResponseDto getProduct(String productId) {
        try {
            return productServiceClient.getProductByProductId(productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal calculateItemTotal(int quantity, BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    private void checkInventoryAvailability(OrderItemReqDto itemReq) {
        try {
            if(!inventoryServiceClient.isStockAvailable(itemReq.getProductId(),itemReq.getQuantity())){
                throw new Exception("Required quantity is not available for "+itemReq.getProductId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal calculateTotalAmount(List<OrderItems> orderItems) {
        return orderItems.stream()
                .map(OrderItems::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<OrderResponseDto> getOrdersByUser(long userId) {
        List<Order> order = Optional.of(orderRepository.findByUserId(userId))
                .filter(list -> !list.isEmpty())
                .orElseThrow(()-> new RuntimeException("Order Not found"));

        return order.stream()
                .map(mapper :: toOrderResponseDto)
                .toList();

    }
}
