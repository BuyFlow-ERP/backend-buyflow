package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;

import java.util.List;
import java.util.Map;

public interface PurchaseRequestService {

    PageResponse<PurchaseRequestDto.ListResponse> getPurchaseRequests(
            String requestNumber,
            String title,
            String requester,
            String department,
            String status,
            String priority,
            String requestedFrom,
            String requestedTo,
            String desiredInboundFrom,
            String desiredInboundTo,
            int page,
            int size
    );

    PurchaseRequestDto.DetailResponse getPurchaseRequestDetail(Long requestId);

    PurchaseRequestDto.SummaryResponse getPurchaseRequestSummary();

    Map<String, Object> getFilterOptions();

    PurchaseRequestDto.DetailResponse createPurchaseRequest(
            PurchaseRequestDto.CreateRequest request
    );

    PurchaseRequestDto.DetailResponse updatePurchaseRequest(
            Long requestId,
            PurchaseRequestDto.UpdateRequest request
    );
    
    List<PurchaseRequestDto.ListResponse> getApprovedRequestsWithoutPaging();
}