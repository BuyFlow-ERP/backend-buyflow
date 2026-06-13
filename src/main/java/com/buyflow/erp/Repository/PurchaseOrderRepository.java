package com.buyflow.erp.Repository;

public class PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplierId(Long supplierId);

    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :id")
    
    Optional<PurchaseOrder> findByIdWithItems(@Param("id") Long id);
}
