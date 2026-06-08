package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.PurchaseRequest;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

}
