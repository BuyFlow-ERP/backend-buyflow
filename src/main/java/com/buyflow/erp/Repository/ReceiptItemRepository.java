package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptItemRepository
        extends JpaRepository<ReceiptItem, Long> {

}