package com.buyflow.erp.Service;

public interface PurchaseOrderService {

    // 발주 등록
    PurchaseOrder createOrder(PurchaseOrderRequest request);


    // 발주 상세 조회 (items 포함)
    PurchaseOrderResponse getOrderWithItems(Long orderId);


    // 발주 목록 조회
    List<PurchaseOrderResponse> getOrderList();


    // 발주 수정 (전체 Item 교체 방식)
    PurchaseOrder updateOrder(Long orderId, PurchaseOrderRequest request);


    // 발주 삭제
    void deleteOrder(Long orderId);
}
