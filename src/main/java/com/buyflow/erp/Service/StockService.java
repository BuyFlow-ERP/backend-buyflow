package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Stock;

public interface StockService {

    // 전체 조회
    List<StockDto.Response> findAllStocks();

    List<StockDto.Response> findStocksByProductId(
            Long productId);

    List<StockDto.Response> findStocksByWarehouseCode(
            String warehouseCode);

    // 재고 수량 변경 및 이력 쌓기
    // void updateStockQuantity(Long stockId, Long amount);

}
