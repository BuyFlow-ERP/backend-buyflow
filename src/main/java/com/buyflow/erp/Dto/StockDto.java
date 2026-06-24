package com.buyflow.erp.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDto {

    private Long id;

    private Long itemId;

    private String itemCode;

    private String itemName;

    private String category;

    private String spec;

    private String unit;

    private String warehouseCode;

    private String warehouseName;

    private Integer currentStock;

    private Integer safetyStock;

    private String lastChangedAt;
}