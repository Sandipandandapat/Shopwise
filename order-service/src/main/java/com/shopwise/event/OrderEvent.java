package com.shopwise.event;

import com.shopwise.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private List<OrderItemEvent> items;
    private BigDecimal totalAmount;
}
