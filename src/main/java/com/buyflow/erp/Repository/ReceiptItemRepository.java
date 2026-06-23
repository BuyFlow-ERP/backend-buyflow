package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.PurchaseRequestItem;
import com.buyflow.erp.Entity.ReceiptItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptItemRepository
                extends JpaRepository<ReceiptItem, Long> {
@Query("""
       select coalesce(sum(r.acceptedQty), 0)
       from ReceiptItem r
       where r.orderItemId = :orderItemId
       and r.receiptItemStatus = 'ACTIVE'
       """)
Long getAcceptedQtySum(
        @Param("orderItemId")
        Long orderItemId
);

Page<ReceiptItem> findAll(Pageable pageable);

@Query("SELECT ri FROM ReceiptItem ri WHERE ri.receiptId = :requestId")
List<ReceiptItem> findByRequestId(
        @Param("requestId") Long requestId
);

List<ReceiptItem> findByReceiptId(Long receiptId);

List<ReceiptItem> findByReceiptItemStatus(
        String receiptItemStatus
);
}
