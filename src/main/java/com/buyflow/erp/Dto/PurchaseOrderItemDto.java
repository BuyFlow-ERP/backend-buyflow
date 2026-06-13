package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

public class PurchaseOrderItemDto {

    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    @Getter
    @Setter
    public static class CreateRequest {

        private Long orderId;
        private Long productId;
        private Long quantity;
        private Double unitPrice;
    }
}