package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Dto.PageResponse;

public interface ApprovalService {

    PageResponse<ApprovalHistoryDto.ListResponse> getApprovals(
            String requestNumber,
            String title,
            String requester,
            String department,
            String status,
            String requestedFrom,
            String requestedTo,
            int page,
            int size
    );

    ApprovalHistoryDto.DetailResponse getApprovalDetail(Long approvalId);

    ApprovalHistoryDto.DetailResponse approve(Long approvalId, ApprovalHistoryDto.DecisionRequest request);

    ApprovalHistoryDto.DetailResponse reject(Long approvalId, ApprovalHistoryDto.DecisionRequest request);

    ApprovalHistoryDto.DetailResponse cancelRequest(Long approvalId);
}
