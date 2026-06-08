package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;

public class StockDto {

    // 생성(입고) 요청 들어올 때 쓰기 위함.
    @Getter
    public static class CreateRequest {
        private Long productId;
        private String WarehouseCode;
        private Integer quantity;
    }

    // 화면에 List 뿌릴 때 쓰기 위함.
    @Getter
    public static class Response {
        private Long stockId;
        private Long productId;
        private String productName; // 품목 ID 대신 품목 이름 담기
        private String WarehouseCode;
        private String warehouseName; // 창고 코드 대신 창고 이름 담기
        private Integer quantity;
        private String stockStatus;
        private LocalDateTime updatedAt;
    }

}
