package com.shopwise.kafka;

import com.shopwise.event.InventoryRejectedEvent;
import com.shopwise.event.InventoryReservedEvent;
import com.shopwise.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryOutcomeConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = "${topic.inventory-reserved}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInventoryReserved(InventoryReservedEvent event){
        orderStatusService.markConfirmed(event.getOrderId());
    }
    @KafkaListener(topics = "${topic.inventory-rejected}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInventoryRejected(InventoryRejectedEvent event){
        orderStatusService.markCancelled(event.getOrderId(), event.getReason());
    }

}
