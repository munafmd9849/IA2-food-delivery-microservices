package com.fooddelivery.ORDER_SERVICE.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse {

    private Long foodId;

    private String foodName;

    private Double price;

    private String category;

    private Boolean availability;

    private Long restaurantId;
}