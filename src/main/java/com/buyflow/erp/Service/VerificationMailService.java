package com.buyflow.erp.Service;

import java.time.LocalDateTime;

public interface VerificationMailService {

    void sendVerificationCode(String email, String purpose, String code, LocalDateTime expiresAt);
}
