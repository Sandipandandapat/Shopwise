package com.shopwise.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryReservedEvent {
    private String eventType; // "INVENTORY_RESERVED"
    private Long orderId;
    private LocalDateTime occurredAt;
//    private String correlationId;
}
