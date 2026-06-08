package com.buyflow.erp.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PURCHASE_REQUEST")
public class PurchaseRequest {
	
	@Id
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name = "REQUEST_NO")
	private String requestNo;
	
	@Column(name = "REQUESTOR_ID")
	private Long requestorId;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "REASON")
	private String reason;
	
	@Column(name = "DUE_DATE")
	private LocalDate dueDate;
	
	@Column(name = "TOTAL_AMOUNT")
	private int totalAmount;
	
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	
	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;
	
	@Column(name = "REQUEST_STATUS")
	private String requestStatus;
	
	@Column(name = "DELETED_YN")
	private String deletedYn;
	

}
