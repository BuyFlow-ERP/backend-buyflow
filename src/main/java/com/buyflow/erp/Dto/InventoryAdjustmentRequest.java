package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAdjustmentRequest {

    private String adjustmentType; // INCREASE / DECREASE
    private Integer quantity;
    private String reason;
}