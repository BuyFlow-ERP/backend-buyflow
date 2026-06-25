package com.buyflow.erp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.Stock;


public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductIdAndWarehouseCode(
            Long productId,
            String warehouseCode);

    List<Stock> findByProductId(
            Long productId);

    List<Stock> findByWarehouseCode(
            String warehouseCode);
}
