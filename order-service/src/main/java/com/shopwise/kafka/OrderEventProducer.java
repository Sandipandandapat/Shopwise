package com.shopwise.kafka;

import com.shopwise.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Value("${order.topic.name}")
    private String topicName;

    public void sendOrderEvent(OrderEvent event){
        kafkaTemplate.send(topicName,event);
    }
}
