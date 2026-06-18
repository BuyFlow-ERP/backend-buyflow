package com.buyflow.erp.Dto;

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
        String desiredInboundAt,
        String createdAt,
        String updatedAt,
        int totalAmount,
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
        String desiredInboundAt,
        String createdAt,
        String updatedAt,
        String priorityLabel,
        String requestStatus,
        String requestStatusLabel,
        String reason,
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
        int expectedUnitPrice,
        int expectedAmount,
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
}