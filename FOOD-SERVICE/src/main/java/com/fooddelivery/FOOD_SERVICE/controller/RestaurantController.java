package com.fooddelivery.FOOD_SERVICE.controller;

import com.fooddelivery.FOOD_SERVICE.entity.Restaurant;
import com.fooddelivery.FOOD_SERVICE.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> create(
            @RequestBody Restaurant restaurant) {

        return ResponseEntity.ok(
                restaurantService.createRestaurant(restaurant)
        );
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {

        return ResponseEntity.ok(
                restaurantService.getAllRestaurants()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                restaurantService.getRestaurant(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> update(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant) {

        return ResponseEntity.ok(
                restaurantService.updateRestaurant(id, restaurant)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.noContent().build();
    }
}