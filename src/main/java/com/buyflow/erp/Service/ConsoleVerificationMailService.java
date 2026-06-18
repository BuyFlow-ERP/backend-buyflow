package com.buyflow.erp.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsoleVerificationMailService implements VerificationMailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleVerificationMailService.class);

    @Override
    public void sendVerificationCode(String email, String purpose, String code, LocalDateTime expiresAt) {
        log.info("[DEV MAIL] purpose={}, to={}, code={}, expiresAt={}", purpose, email, code, expiresAt);
    }
}
