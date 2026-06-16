package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.StockHistory;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
//    To do  : 차후 개발
List<StockHistory> findAllByOrderByHistoryIdDesc();
List<StockHistory>
findByHistoryTypeOrderByHistoryIdDesc(
        String historyType);
}
