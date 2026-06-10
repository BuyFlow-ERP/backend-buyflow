package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequestItemDto {
	
	private Long requestItemId;
	private Long requestId;
	private Long productId;
	private int requestQuantity;
	private int estimatedUnitPrice;
	private String remark;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
