package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String loginId,
        String userName,
        String email,
        String phone,
        String status,
        String useYn,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getLoginId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getUseYn(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

