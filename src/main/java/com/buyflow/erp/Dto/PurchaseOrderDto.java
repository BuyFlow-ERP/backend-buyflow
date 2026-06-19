package com.buyflow.erp.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.buyflow.erp.Entity.PurchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PurchaseOrderDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SearchCondition {
		private String orderStatus;
		private String orderNo;
		private Long requestId;
		private String requestNo;
		private String supplierName;
		private String manager;
		private String userName;
		private String userPhone;
		
		private int page = 0;
		private int size = 10;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {

		private Long supplierId;
		private Long createdBy; // 등록시에만 사용
		private String userName;
        private String userPhone;
		private LocalDateTime dueDate;
		private String orderStatus; // 수정시에 주로 사용
		
		private String orderNo;             // 발주 번호
        private Long requestId;             // 구매 요청 ID
        private String requestNumber;       // 구매 요청 번호
        private String requestTitle;        // 구매 요청 제목
        private String expectedInboundFrom; // 입고 예정일 (시작)
        private String expectedInboundTo;   // 입고 예정일 (종료)
        private String warehouseCode;       // 입고 창고 코드
        private String memo;                // 비고
		private String manager;

		private List<PurchaseOrderDto.Item> items;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Item {
		private Long orderItemId;
		private Long productId;
		private Long quantity;
		private Double unitPrice;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private Long orderId;
		private String orderNo;
		
		private Long requestId;       // 구매 요청 ID
		private String requestNo;     // 구매 요청 번호 (화면 렌더링용)
		private String requestTitle;  // 구매 요청 제목 (상세 모달용)
		
		private Long supplierId;
		private String supplierName;
		private String userName;
		private String userPhone;
		private Long createdBy;
		private LocalDateTime createdAt;
		private String orderStatus;
		private LocalDateTime dueDate;
		private Double totalAmount;
		private String manager;

		private List<PurchaseOrderDto.Item> items;

		public static Response from(PurchaseOrder order) {
			List<PurchaseOrderDto.Item> itemDtos = order.getItems().stream()
					.map(item -> PurchaseOrderDto.Item.builder()
							.orderItemId(item.getOrderItemId())
							.productId(item.getProductId())
							.quantity(item.getQuantity())
							.unitPrice(item.getUnitPrice())
							.build())
					.collect(Collectors.toList());

			return Response.builder()
					.orderId(order.getOrderId())
					.orderNo(order.getOrderNo())
					
					.requestId(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getRequestId() : null)
					.requestNo(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getRequestNo() : "-")
					.requestTitle(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getTitle() : "-")
					
					.supplierId(order.getSupplier() != null ? order.getSupplier().getSupplierId() : null)
					.supplierName(order.getSupplier() != null ? order.getSupplier().getSupplierName() : "-")
					.manager(order.getSupplier() != null ? order.getSupplier().getManager() : "-")
					.createdBy(order.getUser() != null ? order.getUser().getUserId() : null)
					.userName(order.getUser() != null ? order.getUser().getUserName() : "-")
					.userPhone(order.getUser() != null ? order.getUser().getPhone() : "-")
					.createdAt(order.getCreatedAt())
					.orderStatus(order.getOrderStatus())
					.dueDate(order.getDueDate())
					.totalAmount(order.getTotalAmount())
					.items(itemDtos)
					.build();
		}
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ItemResponse {
	    private Long requestItemId;     // 구매 요청 품목 식별자 (리액트 key로 사용됨)
	    private String itemCode;        // 품목 코드
	    private String itemName;        // 품목명
	    private String specification;   // 규격
	    private Integer requestedQuantity; // 요청 수량
	    private Integer orderQuantity;    // 발주 수량 (초기값은 요청 수량과 동일하게 세팅)
	    private String unit;            // 단위
	    private Long unitPrice;         // 공급 단가 (초기값 0 또는 품목 기본 단가)
	}
}
