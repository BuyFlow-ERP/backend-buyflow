package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseOrderDto;

public interface PurchaseOrderService {

	// 수정: 생성(Create) 후 화면에 바로 반영할 수 있도록 Response DTO를 반환합니다.
	PurchaseOrderDto.Response createOrder(PurchaseOrderDto.Request request);

	// 발주 상세 조회 (items 포함)
	PurchaseOrderDto.Response getOrderWithItems(Long orderId);

	// 발주 목록 조회
	PageResponse<PurchaseOrderDto.Response> getOrderList(PurchaseOrderDto.SearchCondition condition);

	// 수정: 수정(Update) 후 최신 데이터를 Response DTO로 반환합니다.
	PurchaseOrderDto.Response updateOrder(Long orderId, PurchaseOrderDto.Request request);

	// 발주 삭제
	void deleteOrder(Long orderId);
}