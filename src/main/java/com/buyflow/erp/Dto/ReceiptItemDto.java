package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

public class ReceiptItemDto {

    @Getter
    @Setter
    public static class CreateRequest {

        private Long receiptId;
        private Long orderItemId;
        private Long productId;

        private Long receiptQty;
        private Long defectQty;
        // private Long acceptedQty;

        private String remark;
        private String loginId;
    }
}