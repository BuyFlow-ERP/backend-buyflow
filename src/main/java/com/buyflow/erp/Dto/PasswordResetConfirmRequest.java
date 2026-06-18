package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
        @NotNull(message = "resetTokenId is required.")
        Long resetTokenId,

        @NotBlank(message = "resetToken is required.")
        String resetToken,

        @NotBlank(message = "newPassword is required.")
        @Size(min = 8, max = 100, message = "newPassword must be between 8 and 100 characters.")
        String newPassword
) {
}
