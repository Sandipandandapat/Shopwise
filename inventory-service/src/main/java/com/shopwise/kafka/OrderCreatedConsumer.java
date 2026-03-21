package com.shopwise.kafka;

import com.shopwise.events.OrderEvent;
import com.shopwise.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryReservationService reservationService;

    @KafkaListener(topics = "${order.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void  onOrderCreated(OrderEvent event){
        try {
            log.info("order event consumed");
            reservationService.reserveForOrder(event,null);
        } catch (Exception e){

        }
    }

}
