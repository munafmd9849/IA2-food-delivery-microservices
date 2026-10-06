package com.fooddelivery.ORDER_SERVICE.repository;

import com.fooddelivery.ORDER_SERVICE.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}