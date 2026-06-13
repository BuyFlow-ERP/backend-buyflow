package com.buyflow.erp.Dto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderResponse {

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
