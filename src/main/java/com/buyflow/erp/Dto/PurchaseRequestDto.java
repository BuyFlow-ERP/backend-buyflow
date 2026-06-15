package com.buyflow.erp.Dto;

import java.util.List;

public class PurchaseRequestDto {

    public record ListResponse(
            Long id,
            String requestNumber,
            String title,
            String requester,
            String department,
            String requestedAt,
            String desiredInboundAt,
            long itemCount,
            int totalAmount,
            String priority,
            String status
    ) {
    }

    public record DetailResponse(
            Long id,
            String requestNumber,
            String title,
            String requester,
            String department,
            String requestedAt,
            String desiredInboundAt,
            String priority,
            String status,
            String reason,
            int totalAmount,
            List<ItemResponse> items,
            List<AttachmentResponse> attachments
    ) {
    }

    public record ItemResponse(
            Long requestItemId,
            Long productId,
            String itemCode,
            String itemName,
            String category,
            String specification,
            int requestQuantity,
            String unit,
            int estimatedUnitPrice,
            int estimatedAmount
    ) {
    }

    public record AttachmentResponse(
            Long attachmentId,
            String fileName,
            String downloadUrl
    ) {
    }

    public record SummaryResponse(
            long total,
            long draft,
            long pending,
            long approved,
            long rejected,
            long ordered
    ) {
    }

    public record CreateRequest(
            String requestNumber,
            Long requestorId,
            String requester,
            String department,
            String requestDate,
            String expectedDate,
            String title,
            String urgency,
            String priority,
            String status,
            String reason,
            List<CreateItemRequest> items
    ) {
    }

    public record CreateItemRequest(
            Long productId,
            Integer requestQuantity,
            Integer quantity,
            Integer estimatedUnitPrice,
            Integer unitPrice,
            String remark
    ) {
        public Integer requestQuantity() {
            return requestQuantity != null ? requestQuantity : quantity;
        }

        public Integer estimatedUnitPrice() {
            return estimatedUnitPrice != null ? estimatedUnitPrice : unitPrice;
        }
    }

}
