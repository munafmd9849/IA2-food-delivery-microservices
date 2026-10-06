package com.fooddelivery.USER_SERVICE.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {
}