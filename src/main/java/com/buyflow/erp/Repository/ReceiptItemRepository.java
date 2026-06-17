package com.buyflow.erp.Repository;

import java.util.List;

import com.buyflow.erp.Entity.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptItemRepository
        extends JpaRepository<ReceiptItem, Long> {

    @Query("""
           select coalesce(sum(r.acceptedQty), 0)
           from ReceiptItem r
           where r.orderItemId = :orderItemId
           """)
    Long getAcceptedQtySum(
            @Param("orderItemId")
            Long orderItemId
    );

    List<ReceiptItem> findByReceiptId(Long receiptId);
}