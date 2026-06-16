package com.buyflow.erp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.PurchaseRequestItem;

public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {

    List<PurchaseRequestItem> findByRequestIdOrderByRequestItemIdAsc(Long requestId);

    long countByRequestId(Long requestId);
}