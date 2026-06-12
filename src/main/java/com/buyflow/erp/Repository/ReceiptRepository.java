package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository
        extends JpaRepository<Receipt, Long> {
}