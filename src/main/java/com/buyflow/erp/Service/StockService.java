package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.StockDto;

public interface StockService {

    List<StockDto> findAllStocks();

    List<StockDto> findStocksByProductId(Long productId);

    List<StockDto> findStocksByWarehouseCode(String warehouseCode);

}