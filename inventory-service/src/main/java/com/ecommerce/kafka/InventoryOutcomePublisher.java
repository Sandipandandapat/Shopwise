package com.ecommerce.kafka;

import com.ecommerce.events.InventoryRejectedEvent;
import com.ecommerce.events.InventoryReservedEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryOutcomePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.inventory-reserved}")
    private String inventoryReservedTopic;

    @Value("${topic.inventory-rejected}")
    private String getInventoryRejectedTopic;

    public void publishReserved(Long orderId, @Nullable String correlationId){
        var event = InventoryReservedEvent.builder()
                .eventType("INVENTORY_RESERVED")
                .orderId(orderId)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(inventoryReservedTopic, orderId.toString(), event)
                .whenComplete((result,ex) -> {
                    if(ex==null){
                        var md = result.getRecordMetadata();
                        System.out.println("Published INVENTORY_RESERVED for orderId: "+event.getOrderId()+
                                " topic: "+md.topic()+" partition: "+md.partition()+" offset: "+ md.offset());
                    } else {
                        System.out.println("Failed to publish INVENTORY_RESERVED for orderId: "+event.getOrderId() + ex);
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
                        System.out.println("Published INVENTORY_REJECTED for orderId: "+event.getOrderId()+
                                " topic: "+md.topic()+" partition: "+md.partition()+" offset: "+ md.offset());
                    } else {
                        System.out.println("Failed to publish INVENTORY_REJECTED for orderId: "+event.getOrderId() + ex);
                    }
                });
    }
}
