package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReceiptItemRepository
                extends JpaRepository<ReceiptItem, Long> {

        @Query("""
                        select coalesce(sum(r.acceptedQty), 0)
                        from ReceiptItem r
                        where r.orderItemId = :orderItemId
                        and r.receiptItemStatus = 'ACTIVE'
                        """)
        Long getAcceptedQtySum(
                        @Param("orderItemId") Long orderItemId);

        List<ReceiptItem> findByReceiptItemStatus(
                        String receiptItemStatus);
}