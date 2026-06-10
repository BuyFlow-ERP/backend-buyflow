package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
}
