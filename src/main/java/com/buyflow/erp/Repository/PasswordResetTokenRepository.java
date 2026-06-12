package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByUserIdAndUsedAtIsNull(Long userId);
}
