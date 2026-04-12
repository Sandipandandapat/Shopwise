package com.shopwise.kafka;

import com.shopwise.event.PaymentEvent;
import com.shopwise.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOutcomeConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = "${topic.payment-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentSuccess(PaymentEvent event){
        System.out.println("Received PAYMENT_SUCCESS for order ID: "+event.getOrderId());
        orderStatusService.markConfirmed(event.getOrderId(),event.getTransactionId());
    }

    @KafkaListener(topics = "${topic.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(PaymentEvent event){
        System.out.println("Received PAYMENT_FAILED for order ID: "+event.getOrderId());
        orderStatusService.markCancelled(event.getOrderId(),event.getFailureReason());
    }

}
