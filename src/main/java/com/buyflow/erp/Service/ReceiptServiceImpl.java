package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import com.buyflow.erp.Repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Repository.ProductRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Receipt> getReceipts() {
        return receiptRepository.findAll();
    }

    @Override
    public ReceiptDto.DetailResponse getReceipt(Long receiptId) {

        String sql = buildListBaseSql()
                + " AND x.RECEIPT_ID = :receiptId";

        Map<String, Object> params = new HashMap<>();
        params.put("receiptId", receiptId);

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    params,
                    (rs, rowNum) -> {

                        ReceiptDto.DetailResponse dto = new ReceiptDto.DetailResponse();

                        dto.setReceiptId(rs.getLong("RECEIPT_ID"));
                        dto.setOrderId(rs.getLong("ORDER_ID"));
                        dto.setOrderNumber(rs.getString("ORDER_NUMBER"));
                        dto.setSupplierName(rs.getString("SUPPLIER_NAME"));
                        dto.setOrderedAt(rs.getString("ORDERED_AT"));
                        dto.setExpectedReceiptAt(rs.getString("EXPECTED_RECEIPT_AT"));
                        dto.setWarehouseName(rs.getString("WAREHOUSE_NAME"));
                        dto.setOrderQuantity(rs.getLong("ORDER_QUANTITY"));
                        dto.setReceivedQuantity(rs.getLong("RECEIVED_QUANTITY"));
                        dto.setRemainingQuantity(rs.getLong("REMAINING_QUANTITY"));
                        dto.setStatus(rs.getString("STATUS"));

                        dto.setItems(
                                getReceiptItems(
                                        rs.getLong("ORDER_ID")));

                        dto.setHistories(
                                getReceiptHistories(
                                        rs.getLong("ORDER_ID")));

                        return dto;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ReceiptDto.DetailResponse getReceiptByOrderId(Long orderId) {

        String sql = buildListBaseSql()
                + " AND x.ORDER_ID = :orderId";

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        return jdbcTemplate.queryForObject(
                sql,
                params,
                (rs, rowNum) -> {

                    ReceiptDto.DetailResponse dto = new ReceiptDto.DetailResponse();

                    dto.setReceiptId(
                            rs.getLong("RECEIPT_ID"));

                    dto.setOrderId(
                            rs.getLong("ORDER_ID"));

                    dto.setOrderNumber(
                            rs.getString("ORDER_NUMBER"));

                    dto.setSupplierName(
                            rs.getString("SUPPLIER_NAME"));

                    dto.setOrderedAt(
                            rs.getString("ORDERED_AT"));

                    dto.setExpectedReceiptAt(
                            rs.getString("EXPECTED_RECEIPT_AT"));

                    dto.setWarehouseName(
                            rs.getString("WAREHOUSE_NAME"));

                    dto.setOrderQuantity(
                            rs.getLong("ORDER_QUANTITY"));

                    dto.setReceivedQuantity(
                            rs.getLong("RECEIVED_QUANTITY"));

                    dto.setRemainingQuantity(
                            rs.getLong("REMAINING_QUANTITY"));

                    dto.setStatus(
                            rs.getString("STATUS"));

                    dto.setItems(
                            getReceiptItems(
                                    rs.getLong("ORDER_ID")));

                    dto.setHistories(
                            getReceiptHistories(
                                    rs.getLong("ORDER_ID")));

                    return dto;
                });
    }

    @Override
    public void saveReceipt(ReceiptDto.ReceiptCreateRequest request) {
        try {
            System.out.println("1. saveReceipt 시작");

            Receipt receipt = new Receipt();

            receipt.setOrderId(request.getOrderId());
            receipt.setWarehouseCode(request.getWarehouseCode());
            receipt.setReceiptNo(request.getReceiptNo());
            receipt.setReceiptDate(request.getReceiptDate());
            receipt.setReceiptStatus(request.getReceiptStatus());
            receipt.setLoginId(request.getLoginId());

            System.out.println("2. save 직전");

            receiptRepository.save(receipt);

            System.out.println("3. save 완료");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ReceiptDto.PageResponse<ReceiptDto.ListResponse> searchReceipts(
            String activeTab,
            String orderNumber,
            String supplierKeyword,
            String itemKeyword,
            String warehouseName,
            String expectedFrom,
            String expectedTo,
            String status,
            int page,
            int size) {
        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? 10 : size;
        int offset = (safePage - 1) * safeSize;

        Map<String, Object> params = new HashMap<>();

        String baseSql = buildListBaseSql();
        String whereSql = buildWhereSql(
                activeTab,
                orderNumber,
                supplierKeyword,
                itemKeyword,
                warehouseName,
                expectedFrom,
                expectedTo,
                status,
                params);

        String countSql = """
                SELECT COUNT(*)
                FROM (
                """ + baseSql + whereSql + """
                )
                """;

        Long totalElements = jdbcTemplate.queryForObject(
                countSql,
                params,
                Long.class);

        params.put("offset", offset);
        params.put("size", safeSize);

        String listSql = baseSql
                + whereSql
                + """
                        ORDER BY
                            CASE x.STATUS
                                WHEN 'DELAYED' THEN 1
                                WHEN 'EXPECTED' THEN 2
                                WHEN 'PARTIAL' THEN 3
                                WHEN 'COMPLETED' THEN 4
                                ELSE 5
                            END,
                            x.EXPECTED_RECEIPT_AT NULLS LAST,
                            x.ID DESC
                        OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY
                        """;

        List<ReceiptDto.ListResponse> items = jdbcTemplate.query(
                listSql,
                params,
                (rs, rowNum) -> {
                    ReceiptDto.ListResponse dto = new ReceiptDto.ListResponse();

                    dto.setId(rs.getLong("ID"));
                    dto.setReceiptId(rs.getLong("RECEIPT_ID"));
                    dto.setOrderId(rs.getLong("ORDER_ID"));
                    dto.setOrderNumber(rs.getString("ORDER_NUMBER"));
                    dto.setSupplierName(rs.getString("SUPPLIER_NAME"));
                    dto.setOrderedAt(rs.getString("ORDERED_AT"));
                    dto.setExpectedReceiptAt(rs.getString("EXPECTED_RECEIPT_AT"));
                    dto.setWarehouseName(rs.getString("WAREHOUSE_NAME"));
                    dto.setItemCount(rs.getLong("ITEM_COUNT"));
                    dto.setOrderQuantity(rs.getLong("ORDER_QUANTITY"));
                    dto.setReceivedQuantity(rs.getLong("RECEIVED_QUANTITY"));
                    dto.setRemainingQuantity(rs.getLong("REMAINING_QUANTITY"));
                    dto.setStatus(rs.getString("STATUS"));

                    return dto;
                });

        long safeTotalElements = totalElements == null ? 0 : totalElements;
        int totalPages = (int) Math.max(1, Math.ceil((double) safeTotalElements / safeSize));

        ReceiptDto.Pagination pagination = new ReceiptDto.Pagination();
        pagination.setPage(safePage);
        pagination.setSize(safeSize);
        pagination.setTotalElements(safeTotalElements);
        pagination.setTotalPages(totalPages);

        ReceiptDto.PageResponse<ReceiptDto.ListResponse> response = new ReceiptDto.PageResponse<>();

        response.setItems(items);
        response.setPagination(pagination);

        return response;
    }

    @Override
    public ReceiptDto.FilterOptionsResponse getFilterOptions() {
        String warehouseSql = """
                SELECT '전체 창고' AS WAREHOUSE_NAME
                FROM DUAL
                UNION
                SELECT WAREHOUSE_NAME
                FROM WAREHOUSE
                WHERE WAREHOUSE_NAME IS NOT NULL
                ORDER BY WAREHOUSE_NAME
                """;

        List<String> warehouses = jdbcTemplate.query(
                warehouseSql,
                Map.of(),
                (rs, rowNum) -> rs.getString("WAREHOUSE_NAME"));

        ReceiptDto.FilterOptionsResponse response = new ReceiptDto.FilterOptionsResponse();

        response.setWarehouses(warehouses);
        response.setStatuses(List.of(
                "전체 상태",
                "EXPECTED",
                "DELAYED",
                "PARTIAL",
                "COMPLETED"));

        return response;
    }

    @Override
    public ReceiptDto.SummaryResponse getSummary() {
        String sql = """
                SELECT
                    NVL(SUM(CASE
                        WHEN x.EXPECTED_RECEIPT_AT = TO_CHAR(TRUNC(SYSDATE), 'YYYY-MM-DD')
                         AND x.STATUS = 'EXPECTED'
                        THEN 1 ELSE 0
                    END), 0) AS TODAY_EXPECTED,

                    0 AS YESTERDAY_DIFFERENCE,

                    NVL(SUM(CASE
                        WHEN x.STATUS = 'DELAYED'
                        THEN 1 ELSE 0
                    END), 0) AS DELAYED,

                    NVL(SUM(CASE
                        WHEN x.STATUS = 'PARTIAL'
                        THEN 1 ELSE 0
                    END), 0) AS PARTIAL,

                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE ROUND(
                            NVL(SUM(CASE WHEN x.STATUS = 'COMPLETED' THEN 1 ELSE 0 END), 0)
                            * 100 / COUNT(*)
                        )
                    END AS PROGRESS_RATE,

                    NVL(SUM(CASE
                        WHEN x.STATUS IN ('EXPECTED', 'DELAYED')
                        THEN 1 ELSE 0
                    END), 0) AS EXPECTED_COUNT,

                    NVL(SUM(CASE
                        WHEN x.STATUS = 'PARTIAL'
                        THEN 1 ELSE 0
                    END), 0) AS PARTIAL_COUNT,

                    NVL(SUM(CASE
                        WHEN x.STATUS = 'COMPLETED'
                        THEN 1 ELSE 0
                    END), 0) AS COMPLETED_COUNT

                FROM (
                """ + buildListBaseSql() + """
                ) x
                """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, Map.of());

        ReceiptDto.TabCounts tabCounts = new ReceiptDto.TabCounts();
        tabCounts.setEXPECTED(toLong(result.get("EXPECTED_COUNT")));
        tabCounts.setPARTIAL(toLong(result.get("PARTIAL_COUNT")));
        tabCounts.setCOMPLETED(toLong(result.get("COMPLETED_COUNT")));

        ReceiptDto.SummaryResponse response = new ReceiptDto.SummaryResponse();
        response.setTodayExpected(toLong(result.get("TODAY_EXPECTED")));
        response.setYesterdayDifference(toLong(result.get("YESTERDAY_DIFFERENCE")));
        response.setDelayed(toLong(result.get("DELAYED")));
        response.setPartial(toLong(result.get("PARTIAL")));
        response.setProgressRate(toLong(result.get("PROGRESS_RATE")));
        response.setTabCounts(tabCounts);

        return response;
    }

    private String buildListBaseSql() {
        return """
                                WITH order_base AS (
                                    SELECT
                                        po.ORDER_ID AS ID,
                                        po.ORDER_ID AS ORDER_ID,
                                        NVL(
                                            po.ORDER_NO,
                                            'PO-2026-' || LPAD(TO_CHAR(po.ORDER_ID), 4, '0')
                                        ) AS ORDER_NUMBER,
                                        NVL(s.SUPPLIER_NAME, '-') AS SUPPLIER_NAME,
                                        TO_CHAR(TRUNC(CAST(po.CREATED_AT AS DATE)), 'YYYY-MM-DD') AS ORDERED_AT,
                                        TO_CHAR(TRUNC(CAST(po.DUE_DATE AS DATE)), 'YYYY-MM-DD') AS EXPECTED_RECEIPT_AT,
                                        COUNT(DISTINCT poi.ORDER_ITEM_ID) AS ITEM_COUNT,
                                        NVL(SUM(NVL(poi.QUANTITY, 0)), 0) AS ORDER_QUANTITY
                                    FROM PURCHASE_ORDER po
                                    LEFT JOIN SUPPLIER s
                                        ON s.SUPPLIER_ID = po.SUPPLIER_ID
                                    LEFT JOIN PURCHASE_ORDER_ITEM poi
                                        ON poi.ORDER_ID = po.ORDER_ID
                                    GROUP BY
                                        po.ORDER_ID,
                                        po.ORDER_NO,
                                        s.SUPPLIER_NAME,
                                        TRUNC(CAST(po.CREATED_AT AS DATE)),
                                        TRUNC(CAST(po.DUE_DATE AS DATE))
                                ),
                receipt_base AS (
                    SELECT
                        poi.ORDER_ID AS ORDER_ID,
                        MAX(r.RECEIPT_ID) AS RECEIPT_ID,
                        NVL(SUM(NVL(ri.ACCEPTED_QTY, ri.RECEIPT_QTY)), 0) AS RECEIVED_QUANTITY,
                        MAX(w.WAREHOUSE_NAME) AS WAREHOUSE_NAME
                                    FROM PURCHASE_ORDER_ITEM poi
                                    LEFT JOIN RECEIPT_ITEM ri
                                        ON ri.ORDER_ITEM_ID = poi.ORDER_ITEM_ID
                                    LEFT JOIN RECEIPT r
                                        ON r.RECEIPT_ID = ri.RECEIPT_ID
                                    LEFT JOIN WAREHOUSE w
                                        ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
                                    GROUP BY poi.ORDER_ID
                                ),
                                list_base AS (
                                    SELECT
                    ob.ID,
                    ob.ORDER_ID,
                    rb.RECEIPT_ID,
                    ob.ORDER_NUMBER,
                                        ob.SUPPLIER_NAME,
                                        ob.ORDERED_AT,
                                        ob.EXPECTED_RECEIPT_AT,
                                        NVL(rb.WAREHOUSE_NAME, '-') AS WAREHOUSE_NAME,
                                        ob.ITEM_COUNT,
                                        ob.ORDER_QUANTITY,
                                        NVL(rb.RECEIVED_QUANTITY, 0) AS RECEIVED_QUANTITY,
                                        GREATEST(
                                            ob.ORDER_QUANTITY - NVL(rb.RECEIVED_QUANTITY, 0),
                                            0
                                        ) AS REMAINING_QUANTITY,
                                        CASE
                                            WHEN NVL(rb.RECEIVED_QUANTITY, 0) >= ob.ORDER_QUANTITY
                                             AND ob.ORDER_QUANTITY > 0
                                            THEN 'COMPLETED'

                                            WHEN NVL(rb.RECEIVED_QUANTITY, 0) > 0
                                            THEN 'PARTIAL'

                                            WHEN ob.EXPECTED_RECEIPT_AT < TO_CHAR(TRUNC(SYSDATE), 'YYYY-MM-DD')
                                            THEN 'DELAYED'

                                            ELSE 'EXPECTED'
                                        END AS STATUS
                                    FROM order_base ob
                                    LEFT JOIN receipt_base rb
                                        ON rb.ORDER_ID = ob.ORDER_ID
                                )
                               SELECT
                    x.ID,
                    x.ORDER_ID,
                    x.RECEIPT_ID,
                    x.ORDER_NUMBER,
                                    x.SUPPLIER_NAME,
                                    x.ORDERED_AT,
                                    x.EXPECTED_RECEIPT_AT,
                                    x.WAREHOUSE_NAME,
                                    x.ITEM_COUNT,
                                    x.ORDER_QUANTITY,
                                    x.RECEIVED_QUANTITY,
                                    x.REMAINING_QUANTITY,
                                    x.STATUS
                                FROM list_base x
                                WHERE 1 = 1
                                """;
    }

    private String buildWhereSql(
            String activeTab,
            String orderNumber,
            String supplierKeyword,
            String itemKeyword,
            String warehouseName,
            String expectedFrom,
            String expectedTo,
            String status,
            Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();

        if (!isBlank(status) && !"전체 상태".equals(status)) {
            sql.append(" AND x.STATUS = :status\n");
            params.put("status", status);
        } else {
            if ("EXPECTED".equals(activeTab)) {
                sql.append(" AND x.STATUS IN ('EXPECTED', 'DELAYED')\n");
            } else if ("PARTIAL".equals(activeTab)) {
                sql.append(" AND x.STATUS = 'PARTIAL'\n");
            } else if ("COMPLETED".equals(activeTab)) {
                sql.append(" AND x.STATUS = 'COMPLETED'\n");
            }
        }

        if (!isBlank(orderNumber)) {
            sql.append(" AND LOWER(x.ORDER_NUMBER) LIKE '%' || LOWER(:orderNumber) || '%'\n");
            params.put("orderNumber", orderNumber.trim());
        }

        if (!isBlank(supplierKeyword)) {
            sql.append(" AND LOWER(x.SUPPLIER_NAME) LIKE '%' || LOWER(:supplierKeyword) || '%'\n");
            params.put("supplierKeyword", supplierKeyword.trim());
        }

        if (!isBlank(warehouseName) && !"전체 창고".equals(warehouseName)) {
            sql.append(" AND x.WAREHOUSE_NAME = :warehouseName\n");
            params.put("warehouseName", warehouseName);
        }

        if (!isBlank(expectedFrom)) {
            sql.append(" AND x.EXPECTED_RECEIPT_AT >= :expectedFrom\n");
            params.put("expectedFrom", expectedFrom);
        }

        if (!isBlank(expectedTo)) {
            sql.append(" AND x.EXPECTED_RECEIPT_AT <= :expectedTo\n");
            params.put("expectedTo", expectedTo);
        }

        if (!isBlank(itemKeyword)) {
            sql.append("""
                    AND EXISTS (
                        SELECT 1
                        FROM PURCHASE_ORDER_ITEM poi2
                        LEFT JOIN PRODUCTS p2
                            ON p2.PRODUCT_ID = poi2.PRODUCT_ID
                        WHERE poi2.ORDER_ID = x.ORDER_ID
                          AND (
                                LOWER(p2.PRODUCT_NO) LIKE '%' || LOWER(:itemKeyword) || '%'
                             OR LOWER(p2.PRODUCT_NAME) LIKE '%' || LOWER(:itemKeyword) || '%'
                          )
                    )
                    """);
            params.put("itemKeyword", itemKeyword.trim());
        }

        return sql.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.parseLong(String.valueOf(value));
    }

    private List<ReceiptDto.ReceiptItemResponse> getReceiptItems(Long orderId) {

        return purchaseOrderItemRepository
                .findByPurchaseOrder_OrderId(orderId)
                .stream()
                .map(orderItem -> {

                    System.out.println(orderItem);
                    Product product = productRepository
                            .findById(orderItem.getProductId())
                            .orElse(null);

                    if (product == null) {
                        return null;
                    }

                    Long receivedQty = receiptItemRepository.getAcceptedQtySum(
                            orderItem.getOrderItemId());

                    if (receivedQty == null) {
                        receivedQty = 0L;
                    }

                    return new ReceiptDto.ReceiptItemResponse(
                            orderItem.getOrderItemId(),
                            product.getProductNo(),
                            product.getProductName(),
                            product.getSpec(),
                            orderItem.getQuantity(),
                            receivedQty,
                            Math.max(
                                    orderItem.getQuantity() - receivedQty,
                                    0),
                            product.getUnit());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<ReceiptDto.HistoryResponse> getReceiptHistories(Long orderId) {

        return receiptRepository.findByOrderId(orderId)
                .stream()
                .map(receipt -> {

                    List<ReceiptItem> receiptItems = receiptItemRepository.findByReceiptId(
                            receipt.getReceiptId());

                    Long totalQty = receiptItems
                            .stream()
                            .mapToLong(item -> item.getAcceptedQty() != null
                                    ? item.getAcceptedQty()
                                    : 0L)
                            .sum();

                    String memo = receiptItems.stream()
                            .map(ReceiptItem::getRemark)
                            .filter(remark -> remark != null && !remark.isBlank())
                            .findFirst()
                            .orElse("");

                    return new ReceiptDto.HistoryResponse(
                            receipt.getReceiptId(),
                            receipt.getReceiptNo(),
                            receipt.getReceiptDate() != null
                                    ? receipt.getReceiptDate().toString()
                                    : "",
                            receipt.getLoginId(),
                            totalQty,
                            memo,
                            new ArrayList<>());

                })
                .toList();
    }
}
