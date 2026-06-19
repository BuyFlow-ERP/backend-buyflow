package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @Query(
        value = """
            SELECT DISTINCT r.*
              FROM RECEIPT r
              LEFT JOIN PURCHASE_ORDER po ON po.ORDER_ID = r.ORDER_ID
              LEFT JOIN SUPPLIER s ON s.SUPPLIER_ID = po.SUPPLIER_ID
              LEFT JOIN WAREHOUSE w ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
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
               AND (:inboundNumber IS NULL OR r.RECEIPT_NO LIKE '%' || :inboundNumber || '%')
               AND (:orderNumber IS NULL OR TO_CHAR(po.ORDER_ID) LIKE '%' || :orderNumber || '%')
               AND (:supplierName IS NULL OR s.SUPPLIER_NAME = :supplierName)
               AND (:warehouseName IS NULL OR w.WAREHOUSE_NAME = :warehouseName)
               AND (:receivedFrom IS NULL OR TRUNC(r.RECEIPT_DATE) >= TO_DATE(:receivedFrom, 'YYYY-MM-DD'))
               AND (:receivedTo IS NULL OR TRUNC(r.RECEIPT_DATE) <= TO_DATE(:receivedTo, 'YYYY-MM-DD'))
             ORDER BY r.RECEIPT_DATE DESC, r.RECEIPT_ID DESC
        """,
        countQuery = """
            SELECT COUNT(DISTINCT r.RECEIPT_ID)
              FROM RECEIPT r
              LEFT JOIN PURCHASE_ORDER po ON po.ORDER_ID = r.ORDER_ID
              LEFT JOIN SUPPLIER s ON s.SUPPLIER_ID = po.SUPPLIER_ID
              LEFT JOIN WAREHOUSE w ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
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
               AND (:inboundNumber IS NULL OR r.RECEIPT_NO LIKE '%' || :inboundNumber || '%')
               AND (:orderNumber IS NULL OR TO_CHAR(po.ORDER_ID) LIKE '%' || :orderNumber || '%')
               AND (:supplierName IS NULL OR s.SUPPLIER_NAME = :supplierName)
               AND (:warehouseName IS NULL OR w.WAREHOUSE_NAME = :warehouseName)
               AND (:receivedFrom IS NULL OR TRUNC(r.RECEIPT_DATE) >= TO_DATE(:receivedFrom, 'YYYY-MM-DD'))
               AND (:receivedTo IS NULL OR TRUNC(r.RECEIPT_DATE) <= TO_DATE(:receivedTo, 'YYYY-MM-DD'))
        """,
        nativeQuery = true
    )
    Page<Receipt> searchPendingReceipts(
            @Param("inboundNumber") String inboundNumber,
            @Param("orderNumber") String orderNumber,
            @Param("supplierName") String supplierName,
            @Param("warehouseName") String warehouseName,
            @Param("receivedFrom") String receivedFrom,
            @Param("receivedTo") String receivedTo,
            Pageable pageable
    );

    @Query(
        value = """
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
        """,
        nativeQuery = true
    )
    long countPendingReceipts();

    @Query(
    value = """
        SELECT DISTINCT r.*
          FROM RECEIPT r
          LEFT JOIN PURCHASE_ORDER po ON po.ORDER_ID = r.ORDER_ID
          LEFT JOIN SUPPLIER s ON s.SUPPLIER_ID = po.SUPPLIER_ID
          LEFT JOIN WAREHOUSE w ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
         WHERE EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NOT EXISTS (
                        SELECT 1
                          FROM INSPECTION i
                         WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                   )
         )
           AND (:inspectionNumber IS NULL OR TO_CHAR(r.RECEIPT_ID) LIKE '%' || :inspectionNumber || '%')
           AND (:inboundNumber IS NULL OR r.RECEIPT_NO LIKE '%' || :inboundNumber || '%')
           AND (:orderNumber IS NULL OR TO_CHAR(po.ORDER_ID) LIKE '%' || :orderNumber || '%')
           AND (:supplierName IS NULL OR s.SUPPLIER_NAME = :supplierName)
           AND (:warehouseName IS NULL OR w.WAREHOUSE_NAME = :warehouseName)
           AND (:receivedFrom IS NULL OR TRUNC(r.RECEIPT_DATE) >= TO_DATE(:receivedFrom, 'YYYY-MM-DD'))
           AND (:receivedTo IS NULL OR TRUNC(r.RECEIPT_DATE) <= TO_DATE(:receivedTo, 'YYYY-MM-DD'))
           AND (
                :inspectionResult IS NULL
                OR (
                    :inspectionResult = 'PASS'
                    AND NOT EXISTS (
                        SELECT 1
                          FROM RECEIPT_ITEM ri
                          JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                         WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                           AND NVL(i.DEFECT_QUANTITY, 0) > 0
                    )
                )
                OR (
                    :inspectionResult = 'DEFECT'
                    AND EXISTS (
                        SELECT 1
                          FROM RECEIPT_ITEM ri
                          JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                         WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                           AND NVL(i.DEFECT_QUANTITY, 0) > 0
                    )
                )
           )
         ORDER BY r.RECEIPT_DATE DESC, r.RECEIPT_ID DESC
    """,
    countQuery = """
        SELECT COUNT(DISTINCT r.RECEIPT_ID)
          FROM RECEIPT r
          LEFT JOIN PURCHASE_ORDER po ON po.ORDER_ID = r.ORDER_ID
          LEFT JOIN SUPPLIER s ON s.SUPPLIER_ID = po.SUPPLIER_ID
          LEFT JOIN WAREHOUSE w ON w.WAREHOUSE_CODE = r.WAREHOUSE_CODE
         WHERE EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NOT EXISTS (
                        SELECT 1
                          FROM INSPECTION i
                         WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                   )
         )
           AND (:inspectionNumber IS NULL OR TO_CHAR(r.RECEIPT_ID) LIKE '%' || :inspectionNumber || '%')
           AND (:inboundNumber IS NULL OR r.RECEIPT_NO LIKE '%' || :inboundNumber || '%')
           AND (:orderNumber IS NULL OR TO_CHAR(po.ORDER_ID) LIKE '%' || :orderNumber || '%')
           AND (:supplierName IS NULL OR s.SUPPLIER_NAME = :supplierName)
           AND (:warehouseName IS NULL OR w.WAREHOUSE_NAME = :warehouseName)
           AND (:receivedFrom IS NULL OR TRUNC(r.RECEIPT_DATE) >= TO_DATE(:receivedFrom, 'YYYY-MM-DD'))
           AND (:receivedTo IS NULL OR TRUNC(r.RECEIPT_DATE) <= TO_DATE(:receivedTo, 'YYYY-MM-DD'))
           AND (
                :inspectionResult IS NULL
                OR (
                    :inspectionResult = 'PASS'
                    AND NOT EXISTS (
                        SELECT 1
                          FROM RECEIPT_ITEM ri
                          JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                         WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                           AND NVL(i.DEFECT_QUANTITY, 0) > 0
                    )
                )
                OR (
                    :inspectionResult = 'DEFECT'
                    AND EXISTS (
                        SELECT 1
                          FROM RECEIPT_ITEM ri
                          JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                         WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                           AND NVL(i.DEFECT_QUANTITY, 0) > 0
                    )
                )
           )
    """,
    nativeQuery = true
)
Page<Receipt> searchCompletedReceipts(
        @Param("inspectionNumber") String inspectionNumber,
        @Param("inboundNumber") String inboundNumber,
        @Param("orderNumber") String orderNumber,
        @Param("supplierName") String supplierName,
        @Param("warehouseName") String warehouseName,
        @Param("receivedFrom") String receivedFrom,
        @Param("receivedTo") String receivedTo,
        @Param("inspectionResult") String inspectionResult,
        Pageable pageable
);

@Query(
    value = """
        SELECT COUNT(DISTINCT r.RECEIPT_ID)
          FROM RECEIPT r
         WHERE EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NOT EXISTS (
                        SELECT 1
                          FROM INSPECTION i
                         WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                   )
         )
    """,
    nativeQuery = true
)
long countCompletedReceipts();

@Query(
    value = """
        SELECT COUNT(DISTINCT r.RECEIPT_ID)
          FROM RECEIPT r
         WHERE EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NOT EXISTS (
                        SELECT 1
                          FROM INSPECTION i
                         WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                   )
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                  JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NVL(i.DEFECT_QUANTITY, 0) > 0
         )
    """,
    nativeQuery = true
)
long countCompletedPassReceipts();

@Query(
    value = """
        SELECT COUNT(DISTINCT r.RECEIPT_ID)
          FROM RECEIPT r
         WHERE EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
         )
           AND NOT EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NOT EXISTS (
                        SELECT 1
                          FROM INSPECTION i
                         WHERE i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                   )
         )
           AND EXISTS (
                SELECT 1
                  FROM RECEIPT_ITEM ri
                  JOIN INSPECTION i ON i.RECEIPT_ITEM_ID = ri.RECEIPT_ITEM_ID
                 WHERE ri.RECEIPT_ID = r.RECEIPT_ID
                   AND NVL(i.DEFECT_QUANTITY, 0) > 0
         )
    """,
    nativeQuery = true
)
long countCompletedDefectReceipts();
}