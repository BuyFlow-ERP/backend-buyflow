package com.buyflow.erp.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockHistoryResponseDto {

    private Long id;

    private String occurredAt;

    private String movementType;

    private String itemCode;

    private String itemName;

    private String warehouseName;

    private Long quantity;

    private Long beforeStock;

    private Long afterStock;

    private String referenceNumber;

    private String reason;

    private String processedBy;
}