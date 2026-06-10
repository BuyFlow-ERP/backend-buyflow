package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdminUserRoleUpdateRequest(
        @NotEmpty(message = "roleIds is required.")
        List<Long> roleIds
) {
}
