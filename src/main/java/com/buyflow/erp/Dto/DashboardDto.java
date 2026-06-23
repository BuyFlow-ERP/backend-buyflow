package com.buyflow.erp.Dto;

import java.util.List;

public class DashboardDto {

    public record Response(
            String lastUpdated,
            List<SummaryItem> summary,
            List<MonthlyReceiptItem> monthlyReceipt,
            List<StockStatusItem> stockStatus,
            List<RecentPurchaseRequestItem> recentRequests,
            long recentRequestTotal,
            List<LowStockItem> lowStockItems,
            long lowStockTotal,
            SummaryDetails summaryDetails
    ) {
    }

    public record SummaryItem(
            String key,
            String label,
            String value,
            String badge,
            String note,
            String tone
    ) {
    }

    public record MonthlyReceiptItem(
            String month,
            long quantity
    ) {
    }

    public record StockStatusItem(
            String name,
            int value,
            String fill
    ) {
    }

    public record RecentPurchaseRequestItem(
            String id,
            String requester,
            String team,
            String date,
            String amount,
            String status
    ) {
    }

    public record LowStockItem(
            long stockId,
            String code,
            String name,
            String warehouse,
            String warehouseCode,
            long current,
            long safety,
            long shortage
    ) {
    }

    public record SummaryDetails(
            List<DelayedOrderItem> delayedOrders,
            List<PendingApprovalItem> pendingApprovals,
            List<ScheduledReceiptItem> scheduledReceipts,
            List<PendingInspectionItem> pendingInspections,
            List<LowStockItem> lowStockItems
    ) {
    }

    public record DelayedOrderItem(
            Long orderId,
            String orderNo,
            String supplierName,
            String dueDate,
            String status,
            String amount
    ) {
    }

    public record PendingApprovalItem(
            Long requestId,
            String requestNo,
            String requester,
            String team,
            String createdAt,
            String amount,
            String status
    ) {
    }

    public record ScheduledReceiptItem(
            Long orderId,
            String orderNo,
            String supplierName,
            String dueDate,
            String status,
            String amount
    ) {
    }

    public record PendingInspectionItem(
            Long receiptId,
            String receiptNo,
            String orderNo,
            String warehouseName,
            String receiptDate,
            long itemCount,
            long receiptQuantity
    ) {
    }
}