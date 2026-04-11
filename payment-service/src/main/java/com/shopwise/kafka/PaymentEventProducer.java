package com.shopwise.kafka;

import com.shopwise.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Value("${topic.payment-success}")
    private String paymentSuccessTopic;
    @Value("${topic.payment-failed}")
    private String paymentFailedTopic;

    public void sendPaymentEvent(PaymentEvent event){
        String topic = event.getEventType().equals("PAYMENT_SUCCESS")
                ? paymentSuccessTopic
                : paymentFailedTopic;

        kafkaTemplate.send(topic, String.valueOf(event.getOrderId()), event)
                .whenComplete((result,ex)->{
                    if(ex==null){
                        System.out.println("Published "+event.getEventType()+" for order id: "+event.getOrderId()+" to topic: "+topic);
                    } else{
                        System.out.println("Failed to publish "+event.getEventType()+" for order id: "+event.getOrderId());
                    }
                });
    }
}
