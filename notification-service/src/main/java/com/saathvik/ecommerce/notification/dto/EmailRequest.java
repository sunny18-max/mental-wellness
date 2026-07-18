package com.saathvik.ecommerce.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank @Email String recipient,
        @NotBlank String subject,
        @NotBlank String body
) {
}
