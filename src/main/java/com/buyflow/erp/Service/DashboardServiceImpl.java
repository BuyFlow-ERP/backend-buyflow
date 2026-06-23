package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.DashboardDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final DateTimeFormatter LAST_UPDATED_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public DashboardDto.Response getDashboard() {
        long delayedOrders = countDelayedOrders();
        long pendingApprovals = countPendingApprovals();
        long scheduledReceipt = countScheduledReceipt();
        long pendingInspections = countPendingInspections();
        long lowStockTotal = countLowStockItems();
        long recentRequestTotal = countRecentPurchaseRequests();

        return new DashboardDto.Response(
                LocalDateTime.now().format(LAST_UPDATED_FORMATTER),
                buildSummary(
                        delayedOrders,
                        pendingApprovals,
                        scheduledReceipt,
                        pendingInspections,
                        lowStockTotal
                ),
                getMonthlyReceipt(),
                getInventoryStatus(),
                getRecentPurchaseRequests(),
                recentRequestTotal,
                getLowStockItems(5),
                lowStockTotal,
                getSummaryDetails()
        );
    }

    private List<DashboardDto.SummaryItem> buildSummary(
            long delayedOrders,
            long pendingApprovals,
            long scheduledReceipt,
            long pendingInspections,
            long lowStockTotal
    ) {
        return List.of(
                new DashboardDto.SummaryItem(
                        "delayedOrders",
                        "납기 지연 발주",
                        formatCount(delayedOrders, "건"),
                        delayedOrders > 0 ? "위험" : "",
                        "발주 지연 항목 조치",
                        delayedOrders > 0 ? "danger" : "default"
                ),
                new DashboardDto.SummaryItem(
                        "pendingApprovals",
                        "승인 대기 요청",
                        formatCount(pendingApprovals, "건"),
                        "",
                        "승인 처리가 필요한 요청",
                        "default"
                ),
                new DashboardDto.SummaryItem(
                        "scheduledReceipt",
                        "입고 예정 건수",
                        formatCount(scheduledReceipt, "건"),
                        "",
                        "이번 주 예정 발주 기준",
                        "default"
                ),
                new DashboardDto.SummaryItem(
                        "pendingInspections",
                        "검수 대기 건수",
                        formatCount(pendingInspections, "건"),
                        "",
                        "품질 검수 우선순위 높음",
                        "default"
                ),
                new DashboardDto.SummaryItem(
                        "lowStock",
                        "안전재고 부족 품목",
                        formatCount(lowStockTotal, "개"),
                        "",
                        "발주 필요 항목 포함",
                        lowStockTotal > 0 ? "danger" : "default"
                )
        );
    }

    private long countDelayedOrders() {
        return countNative("""
                SELECT COUNT(*)
                  FROM PURCHASE_ORDER
                 WHERE DUE_DATE IS NOT NULL
                   AND DUE_DATE < SYSTIMESTAMP
                   AND NVL(ORDER_STATUS, '-') NOT IN (
                        'RECEIVED',
                        'COMPLETED',
                        'CANCELED',
                        'CANCELLED'
                   )
                """);
    }

    private long countPendingApprovals() {
        return countNative("""
                SELECT COUNT(*)
                  FROM PURCHASE_REQUESTS
                 WHERE NVL(DELETED_YN, 'N') = 'N'
                   AND UPPER(NVL(REQUEST_STATUS, 'PENDING_APPROVAL')) IN (
                        'PENDING',
                        'PENDING_APPROVAL',
                        'WAITING',
                        'REQUESTED'
                   )
                """);
    }

    private long countScheduledReceipt() {
        return countNative("""
                SELECT COUNT(*)
                  FROM PURCHASE_ORDER
                 WHERE DUE_DATE IS NOT NULL
                   AND DUE_DATE >= SYSTIMESTAMP
                   AND DUE_DATE < SYSTIMESTAMP + INTERVAL '7' DAY
                   AND NVL(ORDER_STATUS, '-') NOT IN (
                        'RECEIVED',
                        'COMPLETED',
                        'CANCELED',
                        'CANCELLED'
                   )
                """);
    }

    private long countPendingInspections() {
        return countNative("""
                SELECT COUNT(DISTINCT r.RECEIPT_ID)
                  FROM RECEIPT r
                 WHERE EXISTS (
                        SELECT 1
                          FROM RECEIPT_ITEM ri
                         WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                           AND NOT EXISTS (
                                SELECT 1
                                  FROM INSPECTION i
                                 WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                           )
                 )
                """);
    }

    private long countLowStockItems() {
        return countNative("""
                SELECT COUNT(*)
                  FROM STOCK s
                 WHERE NVL(s.SAFETY_STOCK, 0) > 0
                   AND NVL(s.QUANTITY, 0) < NVL(s.SAFETY_STOCK, 0)
                """);
    }

    private long countRecentPurchaseRequests() {
        return countNative("""
                SELECT COUNT(*)
                  FROM PURCHASE_REQUESTS pr
                 WHERE NVL(pr.DELETED_YN, 'N') = 'N'
                """);
    }

    private long countNative(String sql) {
        Object result = entityManager.createNativeQuery(sql).getSingleResult();

        if (result == null) {
            return 0L;
        }

        return ((Number) result).longValue();
    }

    private DashboardDto.SummaryDetails getSummaryDetails() {
        return new DashboardDto.SummaryDetails(
            getDelayedOrderItems(),
            getPendingApprovalItems(),
            getScheduledReceiptItems(),
            getPendingInspectionItems(),
            getLowStockItems(null)
    );
}

    @SuppressWarnings("unchecked")
    private List<DashboardDto.DelayedOrderItem> getDelayedOrderItems() {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT po.ORDER_ID,
                   NVL(po.ORDER_NO, 'PO-' || po.ORDER_ID) AS ORDER_NO,
                   NVL(s.SUPPLIER_NAME, '-') AS SUPPLIER_NAME,
                   po.DUE_DATE,
                   NVL(po.ORDER_STATUS, '-') AS ORDER_STATUS,
                   NVL(po.TOTAL_AMOUNT, 0) AS TOTAL_AMOUNT
              FROM PURCHASE_ORDER po
              LEFT JOIN SUPPLIER s
                ON s.SUPPLIER_ID = po.SUPPLIER_ID
             WHERE po.DUE_DATE IS NOT NULL
               AND po.DUE_DATE < SYSTIMESTAMP
               AND NVL(po.ORDER_STATUS, '-') NOT IN (
                    'RECEIVED',
                    'COMPLETED',
                    'CANCELED',
                    'CANCELLED'
               )
             ORDER BY po.DUE_DATE ASC, po.ORDER_ID DESC
            """).getResultList();

        return rows.stream()
            .map(row -> new DashboardDto.DelayedOrderItem(
                    toLong(row[0]),
                    stringValue(row[1], "PO-" + row[0]),
                    stringValue(row[2], "-"),
                    formatDate(row[3]),
                    toOrderStatusLabel(stringValue(row[4], "")),
                    formatWon(row[5])
            ))
            .toList();
}

    @SuppressWarnings("unchecked")
    private List<DashboardDto.PendingApprovalItem> getPendingApprovalItems() {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT pr.REQUEST_ID,
                   NVL(pr.REQUEST_NO, 'PR-' || pr.REQUEST_ID) AS REQUEST_NO,
                   NVL(u.USER_NAME, '-') AS USER_NAME,
                   NVL(
                       u.DEPARTMENT_NAME,
                       NVL(u.POSITION_NAME, NVL(u.JOB_RANK, '-'))
                   ) AS TEAM,
                   pr.CREATED_AT,
                   NVL(pr.TOTAL_AMOUNT, 0) AS TOTAL_AMOUNT,
                   NVL(pr.REQUEST_STATUS, '-') AS REQUEST_STATUS
              FROM PURCHASE_REQUESTS pr
              LEFT JOIN USERS u
                ON u.USER_ID = pr.REQUESTOR_ID
             WHERE NVL(pr.DELETED_YN, 'N') = 'N'
               AND UPPER(NVL(pr.REQUEST_STATUS, 'PENDING')) IN (
                    'PENDING',
                    'PENDING_APPROVAL',
                    'WAITING',
                    'REQUESTED'
               )
             ORDER BY pr.CREATED_AT DESC, pr.REQUEST_ID DESC
            """).getResultList();

        return rows.stream()
            .map(row -> new DashboardDto.PendingApprovalItem(
                    toLong(row[0]),
                    stringValue(row[1], "PR-" + row[0]),
                    stringValue(row[2], "-"),
                    stringValue(row[3], "-"),
                    formatDate(row[4]),
                    formatWon(row[5]),
                    toRequestStatusLabel(stringValue(row[6], ""))
            ))
            .toList();
}

    @SuppressWarnings("unchecked")
    private List<DashboardDto.ScheduledReceiptItem> getScheduledReceiptItems() {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT po.ORDER_ID,
                   NVL(po.ORDER_NO, 'PO-' || po.ORDER_ID) AS ORDER_NO,
                   NVL(s.SUPPLIER_NAME, '-') AS SUPPLIER_NAME,
                   po.DUE_DATE,
                   NVL(po.ORDER_STATUS, '-') AS ORDER_STATUS,
                   NVL(po.TOTAL_AMOUNT, 0) AS TOTAL_AMOUNT
              FROM PURCHASE_ORDER po
              LEFT JOIN SUPPLIER s
                ON s.SUPPLIER_ID = po.SUPPLIER_ID
             WHERE po.DUE_DATE IS NOT NULL
               AND po.DUE_DATE >= SYSTIMESTAMP
               AND po.DUE_DATE < SYSTIMESTAMP + INTERVAL '7' DAY
               AND NVL(po.ORDER_STATUS, '-') NOT IN (
                    'RECEIVED',
                    'COMPLETED',
                    'CANCELED',
                    'CANCELLED'
               )
             ORDER BY po.DUE_DATE ASC, po.ORDER_ID DESC
            """).getResultList();

        return rows.stream()
            .map(row -> new DashboardDto.ScheduledReceiptItem(
                    toLong(row[0]),
                    stringValue(row[1], "PO-" + row[0]),
                    stringValue(row[2], "-"),
                    formatDate(row[3]),
                    toOrderStatusLabel(stringValue(row[4], "")),
                    formatWon(row[5])
            ))
            .toList();
}

    @SuppressWarnings("unchecked")
    private List<DashboardDto.PendingInspectionItem> getPendingInspectionItems() {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT r.RECEIPT_ID,
                   NVL(r.RECEIPT_NO, 'RCP-' || r.RECEIPT_ID) AS RECEIPT_NO,
                   NVL(po.ORDER_NO, '-') AS ORDER_NO,
                   NVL(w.WAREHOUSE_NAME, r.WAREHOUSE_CODE) AS WAREHOUSE_NAME,
                   r.RECEIPT_DATE,
                   COUNT(ri.RECEIPT_ITEM_ID) AS ITEM_COUNT,
                   NVL(SUM(ri.RECEIPT_QTY), 0) AS RECEIPT_QTY
              FROM RECEIPT r
              JOIN RECEIPT_ITEM ri
                ON ri.RECEIPT_ID = r.RECEIPT_ID
              LEFT JOIN PURCHASE_ORDER po
                ON po.ORDER_ID = r.ORDER_ID
              LEFT JOIN WAREHOUSE w
                ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
             WHERE NOT EXISTS (
                    SELECT 1
                      FROM INSPECTION i
                     WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
             )
             GROUP BY r.RECEIPT_ID,
                      r.RECEIPT_NO,
                      po.ORDER_NO,
                      w.WAREHOUSE_NAME,
                      r.WAREHOUSE_CODE,
                      r.RECEIPT_DATE
             ORDER BY r.RECEIPT_DATE DESC, r.RECEIPT_ID DESC
            """).getResultList();

        return rows.stream()
            .map(row -> new DashboardDto.PendingInspectionItem(
                    toLong(row[0]),
                    stringValue(row[1], "RCP-" + row[0]),
                    stringValue(row[2], "-"),
                    stringValue(row[3], "-"),
                    formatDate(row[4]),
                    toLong(row[5]),
                    toLong(row[6])
            ))
            .toList();
}

    @SuppressWarnings("unchecked")
    private List<DashboardDto.MonthlyReceiptItem> getMonthlyReceipt() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT TO_CHAR(r.RECEIPT_DATE, 'YYYY-MM') AS MONTH_KEY,
                       NVL(SUM(ri.RECEIPT_QTY), 0) AS TOTAL_QTY
                  FROM RECEIPT r
                  LEFT JOIN RECEIPT_ITEM ri
                    ON ri.RECEIPT_ID = r.RECEIPT_ID
                 WHERE r.RECEIPT_DATE >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -5)
                 GROUP BY TO_CHAR(r.RECEIPT_DATE, 'YYYY-MM')
                """).getResultList();

        Map<String, Long> quantityByMonth = new HashMap<>();

        for (Object[] row : rows) {
            quantityByMonth.put(
                    String.valueOf(row[0]),
                    toLong(row[1])
            );
        }

        List<DashboardDto.MonthlyReceiptItem> result = new ArrayList<>();
        YearMonth start = YearMonth.now().minusMonths(5);

        for (int i = 0; i < 6; i++) {
            YearMonth month = start.plusMonths(i);
            String key = month.format(YEAR_MONTH_FORMATTER);

            result.add(new DashboardDto.MonthlyReceiptItem(
                    month.getMonthValue() + "월",
                    quantityByMonth.getOrDefault(key, 0L)
            ));
        }

        return result;
    }

    private List<DashboardDto.InventoryStatusItem> getInventoryStatus() {
        Object[] row = (Object[]) entityManager.createNativeQuery("""
                SELECT COUNT(*) AS TOTAL_COUNT,
                       SUM(
                            CASE
                                WHEN NVL(QUANTITY, 0) <= 0
                                THEN 1 ELSE 0
                            END
                       ) AS OUT_OF_STOCK_COUNT,
                       SUM(
                            CASE
                                WHEN NVL(QUANTITY, 0) > 0
                                 AND NVL(SAFETY_STOCK, 0) > 0
                                 AND NVL(QUANTITY, 0) < NVL(SAFETY_STOCK, 0)
                                THEN 1 ELSE 0
                            END
                       ) AS LOW_STOCK_COUNT
                  FROM STOCK
                """).getSingleResult();

        long total = toLong(row[0]);
        long outOfStock = toLong(row[1]);
        long lowStock = toLong(row[2]);
        long normal = Math.max(total - outOfStock - lowStock, 0);

        return List.of(
                new DashboardDto.InventoryStatusItem(
                        "정상",
                        toPercent(normal, total),
                        "#2f80ed"
                ),
                new DashboardDto.InventoryStatusItem(
                        "안전재고 이하",
                        toPercent(lowStock, total),
                        "#ef4444"
                ),
                new DashboardDto.InventoryStatusItem(
                        "재고 없음",
                        toPercent(outOfStock, total),
                        "#111827"
                )
        );
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDto.RecentPurchaseRequestItem> getRecentPurchaseRequests() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT pr.REQUEST_ID,
                       pr.REQUEST_NO,
                       u.USER_NAME,
                       u.POSITION_NAME,
                       u.JOB_RANK,
                       pr.CREATED_AT,
                       pr.TOTAL_AMOUNT,
                       pr.REQUEST_STATUS
                  FROM PURCHASE_REQUESTS pr
                  LEFT JOIN USERS u
                    ON u.USER_ID = pr.REQUESTOR_ID
                 WHERE NVL(pr.DELETED_YN, 'N') = 'N'
                 ORDER BY pr.CREATED_AT DESC, pr.REQUEST_ID DESC
                """)
                .setMaxResults(5)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardDto.RecentPurchaseRequestItem(
                        stringValue(row[1], "PR-" + row[0]),
                        stringValue(row[2], "-"),
                        firstNonBlank(
                                stringValue(row[3], ""),
                                stringValue(row[4], ""),
                                "-"
                        ),
                        formatDate(row[5]),
                        formatWon(row[6]),
                        toRequestStatusLabel(stringValue(row[7], ""))
                ))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDto.LowStockItem> getLowStockItems(Integer maxResults) {
        Query query = entityManager.createNativeQuery("""
            SELECT s.STOCK_ID,
                   NVL(p.PRODUCT_NO, 'ITEM-' || s.PRODUCT_ID) AS PRODUCT_NO,
                   NVL(p.PRODUCT_NAME, '-') AS PRODUCT_NAME,
                   NVL(w.WAREHOUSE_NAME, s.WAREHOUSE_CODE) AS WAREHOUSE_NAME,
                   s.WAREHOUSE_CODE,
                   NVL(s.QUANTITY, 0) AS CURRENT_QTY,
                   NVL(s.SAFETY_STOCK, 0) AS SAFETY_QTY,
                   NVL(s.SAFETY_STOCK, 0) - NVL(s.QUANTITY, 0) AS SHORTAGE_QTY
              FROM STOCK s
              LEFT JOIN PRODUCTS p
                ON p.PRODUCT_ID = s.PRODUCT_ID
              LEFT JOIN WAREHOUSE w
                ON w.WAREHOUSE_CODE = s.WAREHOUSE_CODE
             WHERE NVL(s.SAFETY_STOCK, 0) > 0
               AND NVL(s.QUANTITY, 0) < NVL(s.SAFETY_STOCK, 0)
             ORDER BY SHORTAGE_QTY DESC, s.STOCK_ID DESC
            """);

        if (maxResults != null) {
        query.setMaxResults(maxResults);
    }

        List<Object[]> rows = query.getResultList();

        return rows.stream()
        .map(row -> new DashboardDto.LowStockItem(
                toLong(row[0]),
                stringValue(row[1], "ITEM-" + row[0]),
                stringValue(row[2], "-"),
                stringValue(row[3], "-"),
                stringValue(row[4], ""),
                toLong(row[5]),
                toLong(row[6]),
                toLong(row[7])
        ))
        .toList();
}

    private int toPercent(long count, long total) {
        if (total <= 0) {
            return 0;
        }

        return (int) Math.round((count * 100.0) / total);
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    private String stringValue(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }

        String text = String.valueOf(value).trim();

        return text.isEmpty() ? fallback : text;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }

        return "-";
    }

    private String formatDate(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime()
                    .toLocalDate()
                    .format(DATE_FORMATTER);
        }

        String text = String.valueOf(value);

        return text.length() >= 10 ? text.substring(0, 10) : text;
    }

    private String formatWon(Object value) {
        BigDecimal amount = toBigDecimal(value);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);

        return numberFormat.format(amount) + "원";
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        return BigDecimal.ZERO;
    }

    private String formatCount(long value, String suffix) {
        return String.format("%02d%s", value, suffix);
    }

    private String toRequestStatusLabel(String status) {
        if (status == null || status.isBlank()) {
            return "승인대기";
        }

        return switch (status.trim().toUpperCase()) {
            case "DRAFT", "PENDING", "PENDING_APPROVAL", "WAITING", "REQUESTED" -> "승인대기";
            case "APPROVED" -> "승인완료";
            case "REJECTED" -> "반려";
            case "ORDERED" -> "발주완료";
            case "CANCELED", "CANCELLED", "CANCEL_REQUESTED" -> "요청취소";
            default -> status;
        };
    }

     private String toOrderStatusLabel(String status) {
    if (status == null || status.isBlank()) {
        return "-";
    }

        return switch (status.trim().toUpperCase()) {
            case "DRAFT" -> "임시저장";
            case "PENDING" -> "발주대기";
            case "EXPECTED" -> "입고예정";
            case "RECEIVED" -> "입고완료";
            case "COMPLETED" -> "완료";
            case "CANCELED", "CANCELLED" -> "취소";
            default -> status;
    };
}
}