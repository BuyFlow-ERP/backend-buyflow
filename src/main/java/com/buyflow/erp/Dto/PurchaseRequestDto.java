package com.buyflow.erp.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequestDto {
	
	private Long requestId;
	private String requestNo;
	private Long requestorId;
	private String title;
	private String reason;
	private LocalDate dueDate;
	private int totalAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String requestStatus;
	private String deletedYn;
}
