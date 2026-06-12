package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    List<EmailVerificationCode> findByUserIdAndPurposeAndConsumedAtIsNull(Long userId, String purpose);
}
