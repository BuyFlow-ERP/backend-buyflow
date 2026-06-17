package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Entity.StockHistory;

public interface StockHistoryService {

    List<StockHistory> getStockHistory();

    List<StockHistory> getStockHistoryByType(
            String historyType);

    StockHistory getStockHistory(
            Long historyId);
}