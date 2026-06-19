package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.StockHistoryResponseDto;

public interface StockHistoryService {

    List<StockHistoryResponseDto> getStockHistory(
        String fromDate,
        String toDate,
        String itemKeyword,
        String warehouseCode,
        String movementType
);

    List<StockHistoryResponseDto> getStockHistoryByType(
            String historyType);

    StockHistoryResponseDto getStockHistory(
            Long historyId);
}