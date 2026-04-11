package com.shopwise.kafka;

import com.shopwise.event.InventoryReservedEvent;
import com.shopwise.event.PaymentRequestEvent;
import com.shopwise.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "${topic.payment-request}",groupId = "${spring.kafka.consumer.group-id")
    public void onInventoryReserved(PaymentRequestEvent event){
        log.info("Received PAYMENT_REQUESTED for orderId {} ", event.getOrderId());
        paymentService.processPayment(event);
    }
}
