package com.fooddelivery.USER_SERVICE.dto;

public record LoginRequest(
        String email,
        String password
) {
}