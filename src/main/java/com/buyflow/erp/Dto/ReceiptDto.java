package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ReceiptDto {

    @Getter
    @Setter
    public static class ReceiptCreateRequest {

        private Long orderId;
        private String warehouseCode;
        private String receiptNo;
        private LocalDateTime receiptDate;
        private String receiptStatus;
        private String loginId;
    }
}