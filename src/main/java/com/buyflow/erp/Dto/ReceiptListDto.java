package com.buyflow.erp.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptListDto {

    private Long id;
    private String orderNumber;
    private String supplierName;
    private String warehouseName;
    private Long itemCount;
    private Long receivedQuantity;
    private String status;
    private String receiptDate;
}