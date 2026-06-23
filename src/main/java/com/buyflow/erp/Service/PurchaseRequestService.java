package com.buyflow.erp.Service;

import java.util.List;
import java.util.Map;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Entity.PurchaseRequest;

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
            String desiredReceiptFrom,
            String desiredReceiptTo,
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


    PurchaseRequestDto.DetailResponse cancelPurchaseRequest(Long requestId);

    void deletePurchaseRequest(Long requestId);
    
	List<PurchaseRequest> getAllRequestsForExcel();

}