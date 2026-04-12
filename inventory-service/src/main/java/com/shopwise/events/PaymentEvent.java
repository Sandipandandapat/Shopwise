package com.shopwise.events;


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
public class PaymentEvent {
    private String eventType;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String transactionId;
    private String failureReason;
    private LocalDateTime occurredAt;
}