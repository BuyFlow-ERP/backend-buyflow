package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;

public class ApprovalHistoryDto {
	
	@Getter
	private Long approvalId;
	private Long requestId;
	private Long approverId;
	private String approvalStatus;
	private String commentText;
	private LocalDateTime approvedAt;
	private int approvalStep;
}
