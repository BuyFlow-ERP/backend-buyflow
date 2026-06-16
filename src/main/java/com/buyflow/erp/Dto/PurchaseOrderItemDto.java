package com.buyflow.erp.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PurchaseOrderItemDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class CreateRequest {

        private Long orderId;
        private Long productId;
        private Long quantity;
        private Double unitPrice;
    }
}