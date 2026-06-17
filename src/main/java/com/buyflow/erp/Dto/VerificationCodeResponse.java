package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

public record VerificationCodeResponse(
        Long verificationId,
        LocalDateTime expiresAt
) {
}
