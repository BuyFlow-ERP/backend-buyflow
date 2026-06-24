package com.buyflow.erp.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryAdjustmentResponse {

    private Long stockId;
    private int beforeStock;
    private int afterStock;
    private int adjustedQuantity;
    private String movementType;
    private String reason;
}