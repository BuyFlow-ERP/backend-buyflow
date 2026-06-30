package com.buyflow.erp.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindLoginIdRequest(
        @NotBlank(message = "이름을 입력하세요.")
        String userName,

        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {
}