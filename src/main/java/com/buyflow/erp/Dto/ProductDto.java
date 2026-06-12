package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

public class ProductDto {

    @Getter
    @Setter
    public static class CreateRequest {

        private Long productId;
        private String productNo;
        private String productName;
        private String companyName;
        private Long unitPrice;
        private String unit;
        private String categoryName;
        private String spec;

    }
}