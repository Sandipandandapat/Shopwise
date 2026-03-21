package com.shopwise.kafka;

import com.shopwise.events.InventoryRejectedEvent;
import com.shopwise.events.InventoryReservedEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryOutcomePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.inventory-reserved}")
    private String inventoryReservedTopic;

    @Value("${topic.inventory-rejected}")
    private String getInventoryRejectedTopic;

    public void publishReserved(Long orderId, Long userId, BigDecimal totalAmount, @Nullable String correlationId){
        var event = InventoryReservedEvent.builder()
                .eventType("INVENTORY_RESERVED")
                .orderId(orderId)
                .userId(userId)
                .totalAmount(totalAmount)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(inventoryReservedTopic, orderId.toString(), event)
                .whenComplete((result,ex) -> {
                    if(ex==null){
                        var md = result.getRecordMetadata();
                        log.info("Published INVENTORY_RESERVED for orderId: {} topic: {} partition: {} offset: {}",event.getOrderId(), md.topic(),md.partition(),md.offset());
                    } else {
                        log.info("Failed to publish INVENTORY_RESERVED for orderId: {}",event.getOrderId());
                    }
                });
    }

    public void publishRejected(Long orderId, String reason, @Nullable String correlationId){
        var event = InventoryRejectedEvent.builder()
                .eventType("INVENTORY_REJECTED")
                .orderId(orderId)
                .reason(reason)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(getInventoryRejectedTopic, orderId.toString(), event)
                .whenComplete((result,ex) -> {
                    if(ex==null){
                        var md = result.getRecordMetadata();
                        log.info("Published INVENTORY_REJECTED for orderId: {} topic: {} partition: {} offset: {}",event.getOrderId(), md.topic(),md.partition(),md.offset());
                    } else {
                        log.info("Failed to publish INVENTORY_REJECTED for orderId: {}",event.getOrderId());
                    }
                });
    }
}
