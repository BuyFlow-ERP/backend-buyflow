package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String loginId,
        String userName,
        String email,
        String phone,
        String departmentName,
        String positionName,
        String jobRank,
        String accountType,
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
                user.getDepartmentName(),
                user.getPositionName(),
                normalizeJobRank(user.getJobRank()),
                toAccountType(user.getJobRank()),
                user.getStatus(),
                user.getUseYn(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private static String normalizeJobRank(String jobRank) {
        return "ADMIN".equalsIgnoreCase(jobRank) ? "ADMIN" : "USER";
    }

    private static String toAccountType(String jobRank) {
        return "ADMIN".equalsIgnoreCase(jobRank) ? "ADMIN" : "USER";
    }
}
