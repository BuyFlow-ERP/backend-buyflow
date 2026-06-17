package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.VerificationCodeResponse;
import com.buyflow.erp.Entity.EmailVerificationCode;
import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Repository.EmailVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final VerificationMailService verificationMailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.verification.code-expiration-minutes:5}")
    private long codeExpirationMinutes;

    @Value("${app.verification.max-attempts:5}")
    private int maxAttempts;

    @Transactional
    public VerificationCodeResponse issue(String purpose, User user) {
        LocalDateTime now = LocalDateTime.now();
        emailVerificationCodeRepository
                .findByUserIdAndPurposeAndConsumedAtIsNull(user.getUserId(), purpose)
                .forEach(previousCode -> previousCode.setConsumedAt(now));

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = now.plusMinutes(codeExpirationMinutes);

        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setPurpose(purpose);
        verificationCode.setUserId(user.getUserId());
        verificationCode.setLoginId(user.getLoginId());
        verificationCode.setEmail(user.getEmail());
        verificationCode.setCodeHash(passwordEncoder.encode(code));
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setAttemptCount(0);
        verificationCode.setCreatedAt(now);

        EmailVerificationCode savedCode = emailVerificationCodeRepository.save(verificationCode);
        verificationMailService.sendVerificationCode(user.getEmail(), purpose, code, expiresAt);

        return new VerificationCodeResponse(savedCode.getVerificationId(), savedCode.getExpiresAt());
    }

    @Transactional
    public EmailVerificationCode verify(String purpose, Long verificationId, String code) {
        EmailVerificationCode verificationCode = emailVerificationCodeRepository.findById(verificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid verification request."));

        if (!purpose.equals(verificationCode.getPurpose()) || verificationCode.getConsumedAt() != null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid verification request.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (verificationCode.getExpiresAt().isBefore(now)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Verification code has expired.");
        }

        if (verificationCode.getAttemptCount() >= maxAttempts) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Verification attempt limit exceeded.");
        }

        if (!passwordEncoder.matches(code, verificationCode.getCodeHash())) {
            verificationCode.setAttemptCount(verificationCode.getAttemptCount() + 1);
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Verification code does not match.");
        }

        verificationCode.setVerifiedAt(now);
        return verificationCode;
    }

    @Transactional
    public void consume(EmailVerificationCode verificationCode) {
        verificationCode.setConsumedAt(LocalDateTime.now());
    }
}
