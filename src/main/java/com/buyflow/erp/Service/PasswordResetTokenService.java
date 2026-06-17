package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.PasswordResetVerifyResponse;
import com.buyflow.erp.Entity.PasswordResetToken;
import com.buyflow.erp.Repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.verification.reset-token-expiration-minutes:10}")
    private long resetTokenExpirationMinutes;

    @Transactional
    public PasswordResetVerifyResponse issue(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(userId)
                .forEach(previousToken -> previousToken.setUsedAt(now));

        String resetToken = generateResetToken();
        LocalDateTime expiresAt = now.plusMinutes(resetTokenExpirationMinutes);

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(userId);
        token.setTokenHash(passwordEncoder.encode(resetToken));
        token.setExpiresAt(expiresAt);
        token.setCreatedAt(now);

        PasswordResetToken savedToken = passwordResetTokenRepository.save(token);

        return new PasswordResetVerifyResponse(savedToken.getResetTokenId(), resetToken, savedToken.getExpiresAt());
    }

    @Transactional
    public PasswordResetToken verify(Long resetTokenId, String resetToken) {
        PasswordResetToken savedToken = passwordResetTokenRepository.findById(resetTokenId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid password reset token."));

        LocalDateTime now = LocalDateTime.now();

        if (savedToken.getUsedAt() != null || savedToken.getExpiresAt().isBefore(now)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Password reset token has expired.");
        }

        if (!passwordEncoder.matches(resetToken, savedToken.getTokenHash())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid password reset token.");
        }

        return savedToken;
    }

    @Transactional
    public void consume(PasswordResetToken resetToken) {
        resetToken.setUsedAt(LocalDateTime.now());
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
