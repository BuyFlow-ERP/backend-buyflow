package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

public class PurchaseOrderItemDto {

    @Getter
    @Setter
    public static class CreateRequest {

        private Long orderId;
        private Long productId;
        private Long quantity;
        private Double unitPrice;
    }
}