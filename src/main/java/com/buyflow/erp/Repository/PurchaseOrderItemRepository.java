package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPurchaseOrder_OrderId(Long orderId);

    void deleteByPurchaseOrder_OrderId(Long orderId);
}