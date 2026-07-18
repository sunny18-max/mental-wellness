package com.saathvik.ecommerce.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReserveRequest(
        @NotNull UUID productId,
        @Positive int quantity
) {
}
