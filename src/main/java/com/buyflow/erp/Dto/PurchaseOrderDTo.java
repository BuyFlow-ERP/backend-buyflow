package com.buyflow.erp.Dto;

public class PurchaseOrderDTo {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseOrderRequest {

        private Long supplierId;
        private Long createdBy;           // 등록시에만 사용
        private LocalDateTime dueDate;
        private String orderStatus;       // 수정시에 주로 사용

        private List<PurchaseOrderItemDto> items;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseOrderResponse {

        private Long orderId;
        private Long supplierId;
        private Long createdBy;
        private LocalDateTime createdAt;
        private String orderStatus;
        private LocalDateTime dueDate;
        private BigDecimal totalAmount;

        private List<PurchaseOrderItemDto> items;

        public static PurchaseOrderResponse from(PurchaseOrder order) {
            List<PurchaseOrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> PurchaseOrderItemDto.builder()
                        .orderItemId(item.getOrderItemId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

            return PurchaseOrderResponse.builder()
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
