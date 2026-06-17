package com.buyflow.erp.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierTradeStatusRequest(
        @NotBlank(message = "거래 상태는 필수입니다.")
        @Size(max = 20, message = "거래 상태는 20자 이하로 입력하세요.")
        String tradeStatus
) {
}
