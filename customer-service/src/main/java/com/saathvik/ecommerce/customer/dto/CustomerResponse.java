package com.saathvik.ecommerce.customer.dto;

import java.util.UUID;

public record CustomerResponse(UUID id, String firstName, String lastName, String email, String role) {
}
