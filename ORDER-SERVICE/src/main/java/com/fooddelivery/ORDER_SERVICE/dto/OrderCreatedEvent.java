package com.fooddelivery.ORDER_SERVICE.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;

    private Long customerId;

    private Double totalAmount;

    private String status;
}