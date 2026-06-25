package com.buyflow.erp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.StockHistory;


public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
//    To do  : 차후 개발
List<StockHistory> findAllByOrderByHistoryIdDesc();
List<StockHistory>
findByHistoryTypeOrderByHistoryIdDesc(
        String historyType);
}
