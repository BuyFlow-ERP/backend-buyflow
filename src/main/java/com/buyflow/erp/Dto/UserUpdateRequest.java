package com.buyflow.erp.Dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 50, message = "사용자명은 50자 이하로 입력하세요.")
        String userName,

        @Size(max = 100, message = "이메일은 100자 이하로 입력하세요.")
        String email,

        @Size(max = 20, message = "연락처는 20자 이하로 입력하세요.")
        String phone,

        @Size(max = 100, message = "부서명은 100자 이하로 입력하세요.")
        String departmentName,

        @Size(max = 50, message = "직책명은 50자 이하로 입력하세요.")
        String positionName,

        @Pattern(regexp = "USER|ADMIN", message = "직급 코드는 USER 또는 ADMIN만 사용할 수 있습니다.")
        String jobRank
) {
}
