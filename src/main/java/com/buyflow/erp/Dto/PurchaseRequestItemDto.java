package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;

public class PurchaseRequestItemDto {
	
	@Getter
	private Long requestItemId;
	private Long requestId;
	private Long productId;
	private int requestQuantity;
	private int estimatedUnitPrice;
	private String remark;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
