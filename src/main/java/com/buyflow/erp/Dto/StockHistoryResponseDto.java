package com.buyflow.erp.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockHistoryResponseDto {

    private Long historyId;

    private String occurredAt;
    private String movementType;

    private String itemCode;
    private String itemName;
    private String warehouseName;
    private String warehouseCode;

    private Long quantity;
    private Long beforeStock;
    private Long afterStock;

    private String referenceNumber;
    private String reason;
    private String processedBy;
}