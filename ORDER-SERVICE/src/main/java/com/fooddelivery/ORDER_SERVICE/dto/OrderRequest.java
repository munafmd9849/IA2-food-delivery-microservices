package com.fooddelivery.ORDER_SERVICE.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private Long customerId;

    private Long restaurantId;

    private List<OrderItemRequest> items;
}