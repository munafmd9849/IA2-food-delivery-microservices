package com.fooddelivery.ORDER_SERVICE.client;

import com.fooddelivery.ORDER_SERVICE.dto.FoodResponse;
import com.fooddelivery.ORDER_SERVICE.security.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "FOOD-SERVICE",
        configuration = FeignConfig.class
)
public interface FoodClient {

    @GetMapping("/api/foods/{id}")
    FoodResponse getFood(@PathVariable("id") Long id);
}