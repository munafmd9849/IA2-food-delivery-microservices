package com.fooddelivery.FOOD_SERVICE.repository;

import com.fooddelivery.FOOD_SERVICE.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    List<Food> findByAvailabilityTrue();
}