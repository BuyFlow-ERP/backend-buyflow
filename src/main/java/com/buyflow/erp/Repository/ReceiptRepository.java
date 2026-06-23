package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository
        extends JpaRepository<Receipt, Long> {
                List<Receipt> findByOrderId(Long orderId);
}