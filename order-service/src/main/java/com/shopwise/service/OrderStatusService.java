package com.shopwise.service;

import com.shopwise.entity.Order;
import com.shopwise.entity.OrderStatus;
import com.shopwise.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;

    @Transactional
    public void markConfirmed(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found "+ orderId));

        if(order.getStatus() == OrderStatus.PENDING ) {
            order.setStatus(OrderStatus.PENDING_FOR_PAYMENT);
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
