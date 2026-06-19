package com.buyflow.erp.Dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;

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
		private String contact;
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
		private String contact;
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
		private String contact;
		private Long createdBy;
		private LocalDateTime createdAt;
		private String orderStatus;
		private LocalDateTime dueDate;
		private Double totalAmount;
		private String manager;

		private List<PurchaseOrderDto.Item> items;

		public static Response from(PurchaseOrder order) {
			if (order == null) return null;

			List<PurchaseOrderDto.Item> itemDtos = new ArrayList<>();
			if (order.getItems() != null) {
				itemDtos = order.getItems().stream()
						.map(item -> PurchaseOrderDto.Item.builder()
								.orderItemId(item.getOrderItemId())
								.productId(item.getProductId())
								.quantity(item.getQuantity())
								.unitPrice(item.getUnitPrice())
								.build())
						.collect(Collectors.toList());
			}

			// 🚀 [총 발주 금액 0원 뚫림 완전 방어선]
			// 만약 DB에서 끄집어낸 totalAmount가 null이거나 0원인데 품목 정보는 살아있다면,
			// 변환기가 실시간으로 수량 * 단가 + 부가세(10%)를 재계산하여 화면에 완벽 동기화해 사출합니다!
			double checkedTotalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
			if (checkedTotalAmount == 0.0 && order.getItems() != null && !order.getItems().isEmpty()) {
				long calcSupply = 0L;
				for (PurchaseOrderItem orderItem : order.getItems()) {
					long qty = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0L;
					double price = orderItem.getUnitPrice() != null ? orderItem.getUnitPrice() : 0.0;
					calcSupply += (long) (qty * price);
				}
				checkedTotalAmount = (double) (calcSupply + (long) Math.floor(calcSupply * 0.1));
			}

			return Response.builder()
					.orderId(order.getOrderId())
					.orderNo(order.getOrderNo())
					
					// 기존 팀원분들이 설계한 구매 요청 엔티티 탯줄 그대로 안전하게 우회 바인딩
					.requestId(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getRequestId() : null)
					.requestNo(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getRequestNo() : "-")
					.requestTitle(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getTitle() : "-")
					
					.supplierId(order.getSupplier() != null ? order.getSupplier().getSupplierId() : null)
					.supplierName(order.getSupplier() != null ? order.getSupplier().getSupplierName() : "-")
					.manager(order.getSupplier() != null ? order.getSupplier().getManager() : "-")
					.createdBy(order.getUser() != null ? order.getUser().getUserId() : null)
					.userName(order.getUser() != null ? order.getUser().getUserName() : "-")
					.userPhone(order.getUser() != null ? order.getUser().getPhone() : "-")
					
					// 🚀 발주일 실시간 누락 안전 가드: 등록/조회 시점 날짜 보존
					.createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
					.orderStatus(order.getOrderStatus())
					.dueDate(order.getDueDate())
					
					// 🚀 자동 복구된 금액 최종 탑재
					.totalAmount(checkedTotalAmount)
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
		private Integer orderQuantity;     // 발주 수량 (초기값은 요청 수량과 동일하게 세팅)
		private String unit;            // 단위
		private Long unitPrice;         // 공급 단가 (초기값 0 또는 품목 기본 단가)
	}
}