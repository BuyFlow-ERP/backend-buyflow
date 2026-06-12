package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerificationCodeVerifyRequest(
        @NotNull(message = "verificationId is required.")
        Long verificationId,

        @NotBlank(message = "code is required.")
        @Pattern(regexp = "^\\d{6}$", message = "code must be 6 digits.")
        String code
) {
}
