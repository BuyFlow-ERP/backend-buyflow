package com.buyflow.erp.Dto;

import jakarta.validation.constraints.Size;

public record AdminUserProfileUpdateRequest(
        @Size(max = 100, message = "부서명은 100자 이하로 입력하세요.")
        String departmentName,

        @Size(max = 50, message = "직책명은 50자 이하로 입력하세요.")
        String positionName,

        // 직급(사원/주임/대리/과장/팀장 등) 원본. USER/ADMIN 정규화하지 않는다.
        @Size(max = 30, message = "직급은 30자 이하로 입력하세요.")
        String jobRank
) {
}