package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.PurchaseRequestItem;

public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {

}
