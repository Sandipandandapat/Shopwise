package com.shopwise.kafka;

import com.shopwise.events.PaymentEvent;
import com.shopwise.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "${topic.payment-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentSuccess(PaymentEvent event){
        log.info("Payment success for order id: {}",event.getOrderId());
        inventoryService.deductStockForOrder(event.getOrderId());
    }

    @KafkaListener(topics = "${topic.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFail(PaymentEvent event){
        log.info("Payment Failed for order id: {}",event.getOrderId());
        inventoryService.releaseStockForOrder(event.getOrderId());
    }

}
