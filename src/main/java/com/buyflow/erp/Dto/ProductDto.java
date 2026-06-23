package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductDto {

    @Getter
    @Setter
    public static class CreateRequest {
        private Long productId;

        // 백엔드 기준 필드
        private String productNo;
        private String productName;
        private String companyName;
        private String categoryName;

        // 프론트 기준 필드 호환용
        private String code;
        private String name;
        private String manufacturer;
        private String category;

        private Long unitPrice;
        private String unit;
        private String spec;
        private String description;
        private Boolean isActive;

        private String bizRegNo;

        // 품목 수정 화면에서 다시 불러오기
        private String parentCategory;
        private String origin;
        private String competingProduct;

        private LocalDate validStartDate;
        private LocalDate validEndDate;

        private String useYn;
    }

    @Getter
    @Setter
    public static class SearchCondition {
        private int page = 0;
        private int size = 15;
        private String itemCode;
        private String itemName;
        private String category;
        private String unit;
        private String activeStatus;
    }

    @Getter
    public static class ListResponse {
        private final Long productId;
        private final String productNo;
        private final String productName;
        private final String companyName;
        private final Long unitPrice;
        private final String unit;
        private final String categoryName;
        private final String spec;
        private final String description;
        private final String useYn;
        private final String createdAt;
        private final String updatedAt;

        // 추가 상세 필드
        private final String bizRegNo;
        private final String parentCategory;
        private final String origin;
        private final String competingProduct;

        private final LocalDate validStartDate;
        private final LocalDate validEndDate;

        // 프론트에서 바로 쓰는 별칭
        private final Long id;
        private final String code;
        private final String name;
        private final String category;
        private final String manufacturer;
        private final Boolean isActive;
        private final String registeredAt;

        private ListResponse(Product product) {
            this.productId = product.getProductId();
            this.productNo = product.getProductNo();
            this.productName = product.getProductName();
            this.companyName = product.getCompanyName();
            this.unitPrice = product.getUnitPrice();
            this.unit = product.getUnit();
            this.categoryName = product.getCategoryName();
            this.spec = product.getSpec();
            this.description = product.getDescription();
            this.useYn = product.getUseYn();

            this.createdAt = product.getCreatedAt() == null
                    ? ""
                    : product.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            this.updatedAt = product.getUpdatedAt() == null
                    ? ""
                    : product.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 추가 상세 필드 매핑
            this.bizRegNo = product.getBizRegNo();
            this.parentCategory = product.getParentCategory();
            this.origin = product.getOrigin();
            this.competingProduct = product.getCompetingProduct();

            this.validStartDate = product.getValidStartDate();
            this.validEndDate = product.getValidEndDate();

            // 프론트 호환용 별칭
            this.id = product.getProductId();
            this.code = product.getProductNo();
            this.name = product.getProductName();
            this.category = product.getCategoryName();
            this.manufacturer = product.getCompanyName();
            this.isActive = !"N".equalsIgnoreCase(product.getUseYn());

            this.registeredAt = product.getCreatedAt() == null
                    ? ""
                    : product.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        public static ListResponse from(Product product) {
            return new ListResponse(product);
        }
    }
}