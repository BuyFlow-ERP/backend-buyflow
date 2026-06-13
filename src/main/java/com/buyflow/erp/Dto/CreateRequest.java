package com.buyflow.erp.Dto;

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
