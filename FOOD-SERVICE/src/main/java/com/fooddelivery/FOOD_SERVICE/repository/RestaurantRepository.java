package com.fooddelivery.FOOD_SERVICE.repository;

import com.fooddelivery.FOOD_SERVICE.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}