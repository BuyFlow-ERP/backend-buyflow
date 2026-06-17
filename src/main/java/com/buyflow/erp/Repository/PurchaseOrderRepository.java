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

<<<<<<< HEAD
    List<PurchaseOrder> findBySupplier_SupplierId(Long supplierId);
=======
    @Query("SELECT p FROM PurchaseOrder p WHERE p.supplier.supplierId = :supplierId")
    List<PurchaseOrder> findBySupplierId(@Param("supplierId") Long supplierId);

>>>>>>> fd12ed1046bd66870c43b779782043113ddfb704
    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :orderId")
    Optional<PurchaseOrder> findByIdWithItems(@Param("orderId") Long orderId);

<<<<<<< HEAD
    @Query("SELECT DISTINCT p FROM PurchaseOrder p " +
            "LEFT JOIN p.supplier s " +
            "LEFT JOIN p.user u " +
            "WHERE (:orderNo IS NULL OR CONCAT(p.orderId, '') LIKE CONCAT('%', CONCAT(:orderNo, '%'))) " +
              "AND (:supplierName IS NULL OR s.supplierName LIKE CONCAT('%', CONCAT(:supplierName, '%'))) " +
            "AND (:userName IS NULL OR u.userName LIKE CONCAT('%', CONCAT(:userName, '%'))) " +
              "AND (:status IS NULL OR p.orderStatus = :status) " +
              "ORDER BY p.orderId DESC")
=======
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
>>>>>>> fd12ed1046bd66870c43b779782043113ddfb704
    Page<PurchaseOrder> searchOrdersAdvanced(
        @Param("orderNo") String orderNo,
        @Param("supplierName") String supplierName,
        @Param("userName") String userName,
        @Param("status") String status,
        Pageable pageable
);
}