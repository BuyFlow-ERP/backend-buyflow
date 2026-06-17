package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdminUserRoleUpdateRequest(
        @NotEmpty(message = "역할을 하나 이상 선택하세요.")
        List<Long> roleIds
) {
}
