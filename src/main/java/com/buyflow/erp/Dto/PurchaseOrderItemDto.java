package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class PurchaseOrderItemDto {

    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public static class CreateRequest {

        private Long orderId;
        private Long productId;
        private Long quantity;
        private Double unitPrice;
    }
}