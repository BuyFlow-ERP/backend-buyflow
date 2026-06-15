package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.PurchaseOrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
	
	List<PurchaseOrderItem> findByOrderId(Long orderId);
	
	void deleteByOrderId(Long orderId);
}