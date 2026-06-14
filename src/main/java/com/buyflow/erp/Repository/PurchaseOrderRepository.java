package com.buyflow.erp.Repository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplierId(Long supplierId);

    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :orderId")
    
    Optional<PurchaseOrder> findByIdWithItems(@Param("orderId") Long orderId);
}
