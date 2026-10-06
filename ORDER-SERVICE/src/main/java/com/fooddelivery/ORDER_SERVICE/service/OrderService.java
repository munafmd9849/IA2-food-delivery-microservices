package com.fooddelivery.ORDER_SERVICE.service;

import com.fooddelivery.ORDER_SERVICE.client.FoodClient;
import com.fooddelivery.ORDER_SERVICE.client.PaymentClient;
import com.fooddelivery.ORDER_SERVICE.dto.*;
import com.fooddelivery.ORDER_SERVICE.entity.Order;
import com.fooddelivery.ORDER_SERVICE.entity.OrderItem;
import com.fooddelivery.ORDER_SERVICE.kafka.OrderEventProducer;
import com.fooddelivery.ORDER_SERVICE.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final FoodClient foodClient;

    private final PaymentClient paymentClient;

    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Order createOrder(OrderRequest request) {

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order must contain at least one item"
            );
        }

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .restaurantId(request.getRestaurantId())
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(0.0)
                .build();

        double totalAmount = 0.0;

        for (OrderItemRequest itemRequest :
                request.getItems()) {

            FoodResponse food =
                    foodClient.getFood(itemRequest.getFoodId());

            if (food == null) {
                throw new RuntimeException(
                        "Food not found: "
                                + itemRequest.getFoodId()
                );
            }

            if (!Boolean.TRUE.equals(
                    food.getAvailability())) {

                throw new RuntimeException(
                        "Food is not available: "
                                + food.getFoodName()
                );
            }

            if (!food.getRestaurantId().equals(
                    request.getRestaurantId())) {

                throw new RuntimeException(
                        "Food does not belong to selected restaurant"
                );
            }

            double itemTotal =
                    food.getPrice()
                            * itemRequest.getQuantity();

            totalAmount += itemTotal;

            OrderItem orderItem =
                    OrderItem.builder()
                            .foodId(food.getFoodId())
                            .quantity(itemRequest.getQuantity())
                            .price(food.getPrice())
                            .order(order)
                            .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        PaymentRequest paymentRequest =
                new PaymentRequest(
                        savedOrder.getOrderId(),
                        totalAmount
                );

        PaymentResponse paymentResponse =
                paymentClient.processPayment(
                        paymentRequest
                );

        if (paymentResponse == null ||
                !"SUCCESS".equalsIgnoreCase(
                        paymentResponse.getPaymentStatus())) {

            savedOrder.setStatus("PAYMENT_FAILED");

            return orderRepository.save(savedOrder);
        }

        savedOrder.setStatus("PLACED");

        savedOrder =
                orderRepository.save(savedOrder);

        OrderCreatedEvent event =
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getOrderId())
                        .customerId(savedOrder.getCustomerId())
                        .totalAmount(savedOrder.getTotalAmount())
                        .status(savedOrder.getStatus())
                        .build();

        orderEventProducer.publishOrderCreated(event);

        return savedOrder;
    }

    public Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getCustomerOrders(
            Long customerId) {

        return orderRepository
                .findByCustomerId(customerId);
    }

    public Order cancelOrder(Long orderId) {

        Order order = getOrder(orderId);

        if ("CANCELLED".equalsIgnoreCase(
                order.getStatus())) {

            return order;
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

}