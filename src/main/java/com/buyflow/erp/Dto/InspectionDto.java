package com.buyflow.erp.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class InspectionDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ListResponse {
		private Long inspectionId;
		private LocalDate inspectionDate;
		private String inspectionType;
		private String inspectionResult;
		private Long receiptItemId;
		private Long userId;
		private Long quantity;
		private Long defectQuantity;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateRequest {

		private Long receiptItemId;
		private Long inspectorId;
		// private Long inspectionQty;
		private LocalDate inspectionDate;
		private String inspectionType;
		private Long quantity;
		
		private Long acceptedQuantity;
		
		private Long defectQty; // front나 입고에서 넘어올 때 매핑
		private Long defectQuantity; //검수 테이블 저장용
		
		private String inspectionResult;
		private String notes;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SearchCondition {
		private Long receiptItemId;
		private String inspectionResult;
		private int page = 0;
		private int size = 10;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResultRequest {
		private Long inspectorId;
		private String inspectorName;
		private LocalDateTime inspectedAt;
		private String note;
		
		private List<ResultItemRequest> items;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResultItemRequest {
		private Long id;
		private Long receiptItemId;
		
		private Long receivedQuantity;
		private Long acceptedQuantity;
		private Long defectiveQuantity;
		
		private String defectReason;
		private String disposition;
	}

}
