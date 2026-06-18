package com.buyflow.erp.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Inspection;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    
    boolean existsByReceiptItemId(Long receiptItemId);

    @Query("SELECT i FROM Inspection i WHERE " + 
            "(:receiptItemId IS NULL OR i.receiptItemId = :receiptItemId) AND " +
            "(:result IS NULL OR i.inspectionResult = :result) " +
            "ORDER BY i.inspectionId DESC")
    Page<Inspection> searchInspections(
            @Param("receiptItemId") Long receiptItemId,
            @Param("result") String result,
            Pageable pageable
    );
    
    long count();
    long countByInspectionResult(String inspectionResult);
}
