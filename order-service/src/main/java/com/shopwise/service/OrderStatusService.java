package com.shopwise.service;

import com.shopwise.entity.Order;
import com.shopwise.entity.OrderStatus;
import com.shopwise.event.InventoryReservedEvent;
import com.shopwise.event.PaymentRequestEvent;
import com.shopwise.kafka.PaymentEventProducer;
import com.shopwise.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public void markPendingForPayment(InventoryReservedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order Not Found: "+event.getOrderId()));

        if (order.getStatus() == OrderStatus.PENDING){
            order.setStatus(OrderStatus.PENDING_FOR_PAYMENT);
            log.info("Order {} marked as PENDING_FOR_PAYMENT", event.getOrderId());

            paymentEventProducer.sendPaymentRequest(
                    PaymentRequestEvent.builder()
                            .eventType("PAYMENT_REQUESTED")
                            .orderId(event.getOrderId())
                            .userId(event.getUserId())
                            .totalAmount(event.getTotalAmount())
                            .occurredAt(event.getOccurredAt())
                            .build()
            );
        }
    }

    @Transactional
    public void markConfirmed(Long orderId, String transactionId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found "+ orderId));

        if(order.getStatus() == OrderStatus.PENDING_FOR_PAYMENT ) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTransactionId(transactionId);
            orderRepository.save(order);
        }
    }

    @Transactional
    public void markCancelled(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found " + orderId));
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

}
