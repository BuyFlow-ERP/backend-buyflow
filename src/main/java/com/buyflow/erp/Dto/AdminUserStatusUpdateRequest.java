package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUserStatusUpdateRequest(
        @NotBlank(message = "계정 상태를 입력하세요.")
        @Pattern(regexp = "PENDING|ACTIVE|LOCKED|INACTIVE", message = "계정 상태는 PENDING, ACTIVE, LOCKED, INACTIVE 중 하나여야 합니다.")
        String status,

        @Pattern(regexp = "Y|N", message = "사용 여부는 Y 또는 N만 사용할 수 있습니다.")
        String useYn
) {
}
