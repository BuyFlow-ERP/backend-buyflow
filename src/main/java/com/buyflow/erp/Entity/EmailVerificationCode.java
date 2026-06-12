package com.buyflow.erp.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "EMAIL_VERIFICATION_CODES")
@SequenceGenerator(
        name = "EMAIL_VERIFICATION_CODES_SEQ_GENERATOR",
        sequenceName = "SEQ_EMAIL_VERIFICATION_CODES",
        allocationSize = 1
)
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_VERIFICATION_CODES_SEQ_GENERATOR")
    @Column(name = "VERIFICATION_ID")
    private Long verificationId;

    @Column(name = "PURPOSE", length = 30, nullable = false)
    private String purpose;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "LOGIN_ID", length = 50)
    private String loginId;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "CODE_HASH", length = 255, nullable = false)
    private String codeHash;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @Column(name = "CONSUMED_AT")
    private LocalDateTime consumedAt;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
