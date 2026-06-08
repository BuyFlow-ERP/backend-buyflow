package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "APPROVAL_HISTORY")
public class ApprovalHistory {

	@Id
	@Column(name = "APPROVAL_ID")
	private Long approvalId;
	
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name = "APPROVER_ID")
	private Long approverId;
	
	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;
	
	@Column(name = "COMMENT_TEXT")
	private String commentText;
	
	@Column(name = "APPOROVED_AT")
	private LocalDateTime approvedAt;
	
	@Column(name = "APPROVAL_STEP")
	private int approvalStep;
}
