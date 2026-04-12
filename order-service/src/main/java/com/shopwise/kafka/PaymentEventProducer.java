package com.shopwise.kafka;

import com.shopwise.event.PaymentRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate;

    @Value("${topic.payment-request}")
    private String paymentRequestTopic;

    public void sendPaymentRequest(PaymentRequestEvent event) {
        kafkaTemplate.send(paymentRequestTopic, String.valueOf(event.getOrderId()),event)
                .whenComplete((res,ex) -> {
                    if(ex == null){
                        log.info("Published PAYMENT_REQUESTED for orderId: {}", event.getOrderId());
                    }else {
                        log.error("Failed to publish PAYMENT_REQUESTED for orderId: {}",
                                event.getOrderId(), ex);
                    }
                });
    }
}
