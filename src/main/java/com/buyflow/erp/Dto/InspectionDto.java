package com.buyflow.erp.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

		private String disposition;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SearchCondition {
		private String inspectionNumber;
	    private String receiptNumber;
	    private String orderNumber;
	    private String supplierName;
	    private String warehouseName;
	    private String priority;
	    private String receivedFrom;
	    private String receivedTo;
	    private String summaryFilter;
		private Long receiptItemId;
		private String inspectionResult;
		
		@Builder.Default
		private Integer page = 1;
		
		@Builder.Default
		private Integer size = 15;
	}
	
	@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;                    // 검수 ID (엔티티의 inspectionId)
        private String inspectionNumber;    // 검수 대기 번호 (예: IQC-2026-0001)
        private String receiptNumber;       // 입고 번호
        private String orderNumber;         // 발주 번호
        private String supplierName;        // 공급업체명
        private String warehouseName;       // 입고 창고명
        private String receivedAt;          // 입고일 (LocalDate를 문자열 포맷으로 변환)
        private String inspectionDueAt;     // 검수 기한
        private String priority;            // 우선순위
        private String status;              // 현재 상태 (PENDING, COMPLETED)
        private String receivedBy;          // 입고 담당자
        
        private Integer itemCount;             // 검수 대상 품목 수
        private Long totalReceivedQuantity;    // 검수 대상 총수량
        
        private List<InspectionItemDto> items; // 상세조회 시 뿌려줄 하위 품목 리스트

				private InspectionResultDto inspectionResult;

    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class InspectionItemDto {
	    // ⭕ 프론트엔드가 상세화면에서 순회(map)할 때 애타게 찾는 찐 이름들입니다.
	    private Long id;                 // 리액트 key값용 식별자 (receiptItemId)
	    private Long receiptItemId;      // 입고 품목 식별자
	    private String itemCode;         // 품목 코드 (Product의 productNo)
	    private String itemName;         // 품목명 (Product의 productName)
	    private String category;         // 카테고리 (Product의 categoryName)
	    private String specification;    // 규격 (Product의 spec)
	    private String unit;             // 단위 (Product의 unit)
	    private String lotNumber;        // LOT 번호 (ReceiptItem의 remark 등으로 대체 가능)
	    
	    private Long receivedQuantity;   // 입고 수량 (ReceiptItem의 receiptQty)
	    private Long acceptedQuantity;   // 합격 수량 (ReceiptItem의 acceptedQty)
	    private Long defectiveQuantity;  // 불량 수량 (ReceiptItem의 defectQty)
	    private String defectReason;     // 불량 사유 (ReceiptItem의 remark 등)
	    private String disposition;      // 처리 방식 (기본값 "NONE")
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
	
	public record SummaryResponse(
			long totalCount,
			long pendingCount,
			long passCount,
			long defectCount
	) {}

	public record PendingSummaryResponse(
        long total,
        long receivedToday,
        long urgent,
        long overdue
) {}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class InspectionResultDto {
    private String status;
    private String inspectorName;
    private LocalDateTime inspectedAt;
    private String note;
    private List<InspectionItemDto> items;
}

}
