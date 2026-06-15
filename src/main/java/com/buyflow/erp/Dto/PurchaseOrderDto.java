package com.buyflow.erp.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
		private Long supplierId;
		private String orderStatus;
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
		private LocalDateTime dueDate;
		private String orderStatus; // 수정시에 주로 사용

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
		private Long supplierId;
		private Long createdBy;
		private LocalDateTime createdAt;
		private String orderStatus;
		private LocalDateTime dueDate;
		private Double totalAmount;

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
					.supplierId(order.getSupplierId())
					.createdBy(order.getCreatedBy())
					.createdAt(order.getCreatedAt())
					.orderStatus(order.getOrderStatus())
					.dueDate(order.getDueDate())
					.totalAmount(order.getTotalAmount())
					.items(itemDtos)
					.build();
		}
	}
}
