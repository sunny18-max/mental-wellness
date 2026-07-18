package com.saathvik.ecommerce.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
        @NotNull UUID productId,
        @Positive int quantity,
        @NotNull @Positive BigDecimal unitPrice
) {
}
