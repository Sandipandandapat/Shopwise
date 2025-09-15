package com.ecommerce.kafka;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final InventoryReservationService reservationService;

    @KafkaListener(topics = "${order.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void  onOrderCreated(OrderEvent event){
        try {
            reservationService.reserveForOrder(event,null);
        } catch (Exception e){

        }
    }

}
