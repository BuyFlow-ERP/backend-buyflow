package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "로그인 ID를 입력하세요.")
        @Size(max = 50, message = "로그인 ID는 50자 이하로 입력하세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력하세요.")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력하세요.")
        String password,

        @NotBlank(message = "사용자명을 입력하세요.")
        @Size(max = 50, message = "사용자명은 50자 이하로 입력하세요.")
        String userName,

        @Size(max = 100, message = "이메일은 100자 이하로 입력하세요.")
        String email,

        @Size(max = 20, message = "연락처는 20자 이하로 입력하세요.")
        String phone,

        @Size(max = 100, message = "부서명은 100자 이하로 입력하세요.")
        String departmentName,

        @Size(max = 50, message = "직책명은 50자 이하로 입력하세요.")
        String positionName
) {
}
