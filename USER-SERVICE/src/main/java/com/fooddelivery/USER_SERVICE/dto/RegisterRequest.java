package com.fooddelivery.USER_SERVICE.dto;

public record RegisterRequest(
        String name,
        String email,
        String password,
        String phone
) {
}