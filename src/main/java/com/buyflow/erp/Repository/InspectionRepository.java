package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Inspection;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    
    boolean existsByReceiptItemId(Long receiptItemId);
}
