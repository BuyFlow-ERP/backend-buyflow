package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    @Query("""
           SELECT pr
           FROM PurchaseRequest pr
           WHERE COALESCE(pr.deletedYn, 'N') <> 'Y'
           ORDER BY pr.requestId DESC
           """)
    List<PurchaseRequest> findActiveRequestsOrderByRequestIdDesc();
    
    @Query("SELECT r FROM PurchaseRequest r WHERE r.requestStatus = :status")
    List<PurchaseRequest> findByRequestStatus(@Param("status") String status);
}