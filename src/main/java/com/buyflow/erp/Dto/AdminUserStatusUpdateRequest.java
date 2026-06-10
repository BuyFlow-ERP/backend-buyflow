package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUserStatusUpdateRequest(
        @NotBlank(message = "status is required.")
        @Pattern(regexp = "PENDING|ACTIVE|LOCKED|INACTIVE", message = "status must be PENDING, ACTIVE, LOCKED, or INACTIVE.")
        String status,

        @Pattern(regexp = "Y|N", message = "useYn must be Y or N.")
        String useYn
) {
}
