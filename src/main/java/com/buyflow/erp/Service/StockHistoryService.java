package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Entity.StockHistory;

public interface StockHistoryService {

    List<StockHistoryResponseDto> getStockHistory();

    List<StockHistoryResponseDto> getStockHistoryByType(
            String historyType);

    StockHistory getStockHistory(
            Long historyId);

    List<StockHistoryResponseDto> searchStockHistory(
            String fromDate,
            String toDate,
            String itemKeyword,
            String warehouseCode,
            String movementType);
}