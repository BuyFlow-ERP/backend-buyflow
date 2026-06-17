package com.buyflow.erp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("SELECT p FROM PurchaseOrder p WHERE p.supplier.supplierId = :supplierId")
    List<PurchaseOrder> findBySupplierId(@Param("supplierId") Long supplierId);

    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :orderId")
    Optional<PurchaseOrder> findByIdWithItems(@Param("orderId") Long orderId);

    @Query(
        value = """
        SELECT DISTINCT p
        FROM PurchaseOrder p
        LEFT JOIN p.supplier s
        LEFT JOIN p.user u
        WHERE (:orderNo IS NULL OR :orderNo = ''
               OR CAST(p.orderId AS string) LIKE CONCAT(CONCAT('%', :orderNo), '%'))
          AND (:supplierName IS NULL OR :supplierName = ''
               OR s.supplierName LIKE CONCAT(CONCAT('%', :supplierName), '%'))
          AND (:userName IS NULL OR :userName = ''
               OR u.userName LIKE CONCAT(CONCAT('%', :userName), '%'))
          AND (:status IS NULL OR :status = ''
               OR p.orderStatus = :status)
        ORDER BY p.orderId DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT p)
        FROM PurchaseOrder p
        LEFT JOIN p.supplier s
        LEFT JOIN p.user u
        WHERE (:orderNo IS NULL OR :orderNo = ''
               OR CAST(p.orderId AS string) LIKE CONCAT(CONCAT('%', :orderNo), '%'))
          AND (:supplierName IS NULL OR :supplierName = ''
               OR s.supplierName LIKE CONCAT(CONCAT('%', :supplierName), '%'))
          AND (:userName IS NULL OR :userName = ''
               OR u.userName LIKE CONCAT(CONCAT('%', :userName), '%'))
          AND (:status IS NULL OR :status = ''
               OR p.orderStatus = :status)
        """
    )
    Page<PurchaseOrder> searchOrdersAdvanced(
        @Param("orderNo") String orderNo,
        @Param("supplierName") String supplierName,
        @Param("userName") String userName,
        @Param("status") String status,
        Pageable pageable
);
}