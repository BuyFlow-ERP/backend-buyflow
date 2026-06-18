package com.buyflow.erp.Dto;

import java.math.BigDecimal;
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
           String createdAt,
           String updatedAt,
           long itemCount,
           BigDecimal totalAmount,
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
          String createdAt,
          String updatedAt,
          String priority,
          String status,
          String reason,
          BigDecimal totalAmount,
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
          BigDecimal estimatedUnitPrice,
          BigDecimal estimatedAmount,
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
            BigDecimal estimatedUnitPrice,
            BigDecimal unitPrice,
            String remark
    ) {
        public Integer requestQuantity() {
           return requestQuantity != null ? requestQuantity : quantity;
        }

        public BigDecimal estimatedUnitPrice() {
           return estimatedUnitPrice != null ? estimatedUnitPrice : unitPrice;
        }
    }

}
