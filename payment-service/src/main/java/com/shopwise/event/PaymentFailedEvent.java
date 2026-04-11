package com.shopwise.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private Long orderId;
    private String razorpayOrderId;
    private String failureReason;
}
