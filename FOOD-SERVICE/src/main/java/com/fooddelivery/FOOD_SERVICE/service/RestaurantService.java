package com.fooddelivery.FOOD_SERVICE.service;

import com.fooddelivery.FOOD_SERVICE.entity.Restaurant;
import com.fooddelivery.FOOD_SERVICE.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {

        Restaurant existing = getRestaurant(id);

        existing.setRestaurantName(restaurant.getRestaurantName());
        existing.setAddress(restaurant.getAddress());
        existing.setPhone(restaurant.getPhone());

        return restaurantRepository.save(existing);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}