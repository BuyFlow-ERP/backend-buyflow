package com.buyflow.erp.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InboundDto {

    private Long id;

    private String orderNumber;

    private String supplierName;

    private String orderedAt;

    private String expectedInboundAt;

    private String warehouseName;

    private Integer itemCount;

    private Long orderQuantity;

    private Long receivedQuantity;

    private Long remainingQuantity;

    private String status;
}