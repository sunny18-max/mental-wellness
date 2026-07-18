package com.saathvik.ecommerce.customer.dto;

public record AuthResponse(String token, String email, String role) {
}
