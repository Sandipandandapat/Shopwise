package com.shopwise.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestEvent {
    private String eventType;
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private LocalDateTime occurredAt;
}
