package com.fooddelivery.ORDER_SERVICE.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    private Long foodId;

    private Integer quantity;
}