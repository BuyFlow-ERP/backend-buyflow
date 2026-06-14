package com.buyflow.erp.Dto;

import java.time.LocalDate;

import lombok.Getter;

public class InspectionDto {

    @Getter
    @Setter
    public static class ListResponse {
        private Long inspectionId;
        private LocalDate inspectionDate;
        private String inspectionType;
        private String inspectionResult;
        private Long receiptItemId;
        private Long userId;
        private Long quantity;
        private Long defectQuantity;
    }

    @Getter
    @Setter
    public static class CreateRequest {

        private Long receiptItemId;

        private Long inspectorId;

        // private Long inspectionQty;

        private Long defectQty;

        private String insepctionResult;

        private String notes;
    }
    
}
