package com.fooddelivery.ORDER_SERVICE.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;

    private Long orderId;

    private Double amount;

    private String paymentStatus;

    private String paymentDate;
}