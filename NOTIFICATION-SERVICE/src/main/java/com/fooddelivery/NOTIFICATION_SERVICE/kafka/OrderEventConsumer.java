package com.fooddelivery.NOTIFICATION_SERVICE.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-group"
    )
    public void consumeOrderCreated(String message) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("        NEW ORDER NOTIFICATION");
        System.out.println("==========================================");
        System.out.println("Order Event: " + message);
        System.out.println("Order confirmation sent successfully!");
        System.out.println("==========================================");
        System.out.println();
    }
}