package com.buyflow.erp.Dto;

import java.math.BigDecimal;
import java.util.List;

public class ApprovalHistoryDto {

    public record ListResponse(
        Long approvalId,
        Long requestId,
        String requestNumber,
        String title,
        String requester,
        String department,
        String requestedAt,
        String desiredReceiptAt,
        String createdAt,
        String updatedAt,
        BigDecimal totalAmount,
        String priority,
        String requestStatus,
        String requestStatusLabel,
        String approvalStep,
        String approver
) {
}

    public record DetailResponse(
        Long approvalId,
        Long requestId,
        String requestNumber,
        String title,
        UserInfo requester,
        DepartmentInfo requestDepartment,
        String requestedAt,
        String desiredReceiptAt,
        String createdAt,
        String updatedAt,
        String priorityLabel,
        String requestStatus,
        String requestStatusLabel,
        String reason,
        boolean canProcess,
        List<ApprovalItemResponse> items,
        List<AttachmentResponse> attachments,
        CurrentStep currentStep,
        List<HistoryResponse> history
) {
}

    public record UserInfo(
            Long id,
            String name,
            String position
    ) {
    }

    public record DepartmentInfo(
            Long id,
            String name
    ) {
    }

    public record CurrentStep(
            String stepLabel,
            UserInfo approver
    ) {
    }

    public record ApprovalItemResponse(

        Long requestItemId,
        Long productId,
        String itemCode,
        String itemName,
        String category,
        String specification,
        int quantity,
        String unit,
        BigDecimal expectedUnitPrice,
        BigDecimal expectedAmount,
        String remark,
        String createdAt,
        String updatedAt
) {
}
    public record AttachmentResponse(
            Long attachmentId,
            String fileName,
            String downloadUrl
    ) {
    }

    public record HistoryResponse(
            Long historyId,
            String status,
            String title,
            String actorName,
            String actorPosition,
            String processedAt,
            String description
    ) {
    }

    public record DecisionRequest(
            String comment
    ) {
    }

    public record SummaryResponse(
        long total,
        long pending,
        long rejected,
        long approved
    ) {   
    }
}