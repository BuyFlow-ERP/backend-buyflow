package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserDepartmentAuthorizationUpdateRequest(
        @NotNull(message = "Department authorization value is required.")
        Boolean authorized
) {
}
