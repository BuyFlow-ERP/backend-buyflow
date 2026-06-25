package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "로그인 ID를 입력하세요.")
        @Size(min = 4, max = 50, message = "로그인 ID는 4자 이상 50자 이하로 입력하세요.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "로그인 ID는 영문, 숫자, 점, 밑줄, 하이픈만 입력할 수 있습니다.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력하세요.")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력하세요.")
        String password,

        @NotBlank(message = "사용자명을 입력하세요.")
        @Size(max = 50, message = "사용자명은 50자 이하로 입력하세요.")
        @Pattern(regexp = "^[가-힣A-Za-z ]+$", message = "사용자명은 한글, 영문, 공백만 입력할 수 있습니다.")
        String userName,

        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "올바른 이메일 형식으로 입력하세요.")
        @Size(max = 100, message = "이메일은 100자 이하로 입력하세요.")
        String email,

        @Size(max = 20, message = "연락처는 20자 이하로 입력하세요.")
        @Pattern(regexp = "^$|^[0-9+() -]+$", message = "연락처는 숫자와 일부 기호만 입력할 수 있습니다.")
        String phone,

        @NotBlank(message = "부서명을 입력하세요.")
        @Size(max = 100, message = "부서명은 100자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(구매팀|물류운영팀|시스템관리팀|영업팀|재고관리팀)$",
                message = "등록 가능한 부서를 선택하세요."
        )
        String departmentName,

        @Size(max = 50, message = "직책명은 50자 이하로 입력하세요.")
        @Pattern(regexp = "^$|^[가-힣A-Za-z0-9 ()_-]+$", message = "직책명에 사용할 수 없는 문자가 포함되어 있습니다.")
        String positionName,

        @NotBlank(message = "직급을 입력하세요.")
        @Size(max = 30, message = "직급은 30자 이하로 입력하세요.")
        @Pattern(regexp = "^(사원|주임|대리|과장)$", message = "등록 가능한 직급을 선택하세요.")
        String jobRank
) {
}
