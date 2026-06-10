package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalHistoryDto {
	
	private Long approvalId;
	private Long requestId;
	private Long approverId;
	private String approvalStatus;
	private String commentText;
	private LocalDateTime approvedAt;
	private int approvalStep;
}
