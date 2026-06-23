package com.buyflow.erp.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDto {

    @Getter
    @Setter
    public static class ReceiptCreateRequest {

        private String receiptNumber;

        private Long targetReceiptId;

        private String receivedAt;

        private String receiverName;

        private String memo;

        private List<ReceiptCreateItemRequest> items;
    }

    @Getter
    @Setter
    public static class ReceiptCreateItemRequest {

        private Long orderItemId;

        private Long receivedQuantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;
        private Long receiptId;
        private Long orderId;
        private String orderNumber;
        private String supplierName;
        private String orderedAt;
        private String expectedReceiptAt;
        private String warehouseName;
        private Long itemCount;
        private Long orderQuantity;
        private Long receivedQuantity;
        private Long remainingQuantity;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResponse {

        private Long receiptId;
        private Long orderId;

        private String orderNumber;
        private String supplierName;

        private String orderedAt;
        private String expectedReceiptAt;

        private String warehouseName;

        private Long orderQuantity;
        private Long receivedQuantity;
        private Long remainingQuantity;

        private String status;

        private List<ReceiptItemResponse> items;
        private List<HistoryResponse> histories;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryResponse {

        private Long id;

        private String receiptNumber;

        private String receivedAt;

        private String receiverName;

        private Long totalReceivedQuantity;

        private String memo;

        private List<Object> attachments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptItemResponse {

        private Long orderItemId;

        private String itemCode;

        private String itemName;

        private String specification;

        private Long orderQuantity;

        private Long cumulativeReceivedQuantity;

        private Long remainingQuantity;

        private String unit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResponse<T> {
        private List<T> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterOptionsResponse {
        private List<String> warehouses;
        private List<String> statuses;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormOptionsResponse {

        private String nextReceiptNumber;

        private List<ListResponse> eligibleOrders;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private long todayExpected;
        private long yesterdayDifference;
        private long delayed;
        private long partial;
        private long progressRate;
        private TabCounts tabCounts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormOptionsResponse {

        private String nextReceiptNumber;

        private List<ListResponse> eligibleOrders;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TabCounts {
        private long EXPECTED;
        private long PARTIAL;
        private long COMPLETED;
    }
}