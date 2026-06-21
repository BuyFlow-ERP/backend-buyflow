package com.buyflow.erp.Dto;

import java.util.List;

public class DashboardDto {

    public record Response(
            String lastUpdated,
            List<SummaryItem> summary,
            List<MonthlyReceiptItem> monthlyReceipt,
            List<InventoryStatusItem> inventoryStatus,
            List<RecentPurchaseRequestItem> recentRequests,
            long recentRequestTotal,
            List<LowStockItem> lowStockItems,
            long lowStockTotal
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

    public record InventoryStatusItem(
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
}