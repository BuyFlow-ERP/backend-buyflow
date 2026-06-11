package com.buyflow.erp.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "loginId is required.")
        @Size(max = 50, message = "loginId must be 50 characters or less.")
        String loginId,

        @NotBlank(message = "userName is required.")
        @Size(max = 50, message = "userName must be 50 characters or less.")
        String userName,

        @NotBlank(message = "email is required.")
        @Email(message = "email must be valid.")
        @Size(max = 100, message = "email must be 100 characters or less.")
        String email,

        @NotBlank(message = "phone is required.")
        @Size(max = 20, message = "phone must be 20 characters or less.")
        String phone,

        @NotBlank(message = "newPassword is required.")
        @Size(min = 8, max = 100, message = "newPassword must be between 8 and 100 characters.")
        String newPassword
) {
}
