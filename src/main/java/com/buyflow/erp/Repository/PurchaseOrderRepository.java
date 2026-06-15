package com.buyflow.erp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :orderId")
    Optional<PurchaseOrder> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT p FROM PurchaseOrder p WHERE " +
            "(:supplierId IS NULL OR p.supplierId = :supplierId) AND " +
            "(:status IS NULL OR p.orderStatus = :status) " +
            "ORDER BY p.orderId DESC")
    Page<PurchaseOrder> searchOrders(
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
            Pageable pageable
    );
}
