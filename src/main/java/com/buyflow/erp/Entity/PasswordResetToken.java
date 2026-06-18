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
@Table(name = "PASSWORD_RESET_TOKENS")
@SequenceGenerator(
        name = "PASSWORD_RESET_TOKENS_SEQ_GENERATOR",
        sequenceName = "SEQ_PASSWORD_RESET_TOKENS",
        allocationSize = 1
)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PASSWORD_RESET_TOKENS_SEQ_GENERATOR")
    @Column(name = "RESET_TOKEN_ID")
    private Long resetTokenId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "TOKEN_HASH", length = 255, nullable = false)
    private String tokenHash;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "USED_AT")
    private LocalDateTime usedAt;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
