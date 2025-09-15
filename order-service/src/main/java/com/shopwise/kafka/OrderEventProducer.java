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
        String key = String.valueOf(event.getOrderId());
        kafkaTemplate.send(topicName, key, event)
                .whenComplete((result,ex) -> {
                    if(ex==null){
                        var md = result.getRecordMetadata();
                        System.out.println("Published ORDER_CREATED orderId: "+event.getOrderId()+
                                " topic: "+md.topic()+" partition: "+md.partition()+" offset: "+ md.offset());
                    } else {
                        System.out.println("Failed to publish ORDER_CREATED orderId: "+event.getOrderId() + ex);
                    }
                });
    }
}
