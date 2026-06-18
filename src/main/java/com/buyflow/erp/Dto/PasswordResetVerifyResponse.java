package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

public record PasswordResetVerifyResponse(
        Long resetTokenId,
        String resetToken,
        LocalDateTime expiresAt
) {
}
