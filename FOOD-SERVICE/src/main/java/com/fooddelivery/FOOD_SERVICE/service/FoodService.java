package com.fooddelivery.FOOD_SERVICE.service;

import com.fooddelivery.FOOD_SERVICE.entity.Food;
import com.fooddelivery.FOOD_SERVICE.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    @Cacheable(value = "foods", key = "#id")
    public Food getFood(Long id) {

        System.out.println("CACHE MISS - Fetching food from MySQL");

        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }

    @Cacheable(value = "foodList")
    public List<Food> getAllFoods() {

        System.out.println("CACHE MISS - Fetching all foods from MySQL");

        return foodRepository.findAll();
    }

    public List<Food> getFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId);
    }

    @CacheEvict(value = "foodList", allEntries = true)
    public Food createFood(Food food) {

        return foodRepository.save(food);
    }

    @CacheEvict(value = {"foods", "foodList"}, key = "#id")
    public Food updateFood(Long id, Food food) {

        Food existing = getFood(id);

        existing.setFoodName(food.getFoodName());
        existing.setPrice(food.getPrice());
        existing.setCategory(food.getCategory());
        existing.setAvailability(food.getAvailability());
        existing.setRestaurantId(food.getRestaurantId());

        return foodRepository.save(existing);
    }

    @CacheEvict(value = {"foods", "foodList"}, key = "#id")
    public void deleteFood(Long id) {

        foodRepository.deleteById(id);

        System.out.println("Food deleted and cache evicted");
    }
}