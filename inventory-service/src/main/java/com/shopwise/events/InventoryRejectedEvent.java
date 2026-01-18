package com.shopwise.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryRejectedEvent {
    private String eventType;
    private Long orderId;
    private String reason;
    private LocalDateTime occurredAt;
}
