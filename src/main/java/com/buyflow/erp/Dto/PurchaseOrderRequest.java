package com.buyflow.erp.Dto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderRequest {

    private Long supplierId;
    private Long createdBy;           // 등록시에만 사용
    private LocalDateTime dueDate;
    private String orderStatus;       // 수정시에 주로 사용

    private List<PurchaseOrderItemDto> items;
}
