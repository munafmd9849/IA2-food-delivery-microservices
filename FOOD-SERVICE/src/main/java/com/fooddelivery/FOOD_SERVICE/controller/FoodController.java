package com.fooddelivery.FOOD_SERVICE.controller;

import com.fooddelivery.FOOD_SERVICE.entity.Food;
import com.fooddelivery.FOOD_SERVICE.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<Food> create(
            @RequestBody Food food) {

        return ResponseEntity.ok(
                foodService.createFood(food)
        );
    }

    @GetMapping
    public ResponseEntity<List<Food>> getAll() {

        return ResponseEntity.ok(
                foodService.getAllFoods()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> get(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                foodService.getFood(id)
        );
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getByRestaurant(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                foodService.getFoodsByRestaurant(restaurantId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> update(
            @PathVariable Long id,
            @RequestBody Food food) {

        return ResponseEntity.ok(
                foodService.updateFood(id, food)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        foodService.deleteFood(id);

        return ResponseEntity.noContent().build();
    }
}