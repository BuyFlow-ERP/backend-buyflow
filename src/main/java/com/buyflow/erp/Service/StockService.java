package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.StockDto;

public interface StockService {

    // 전체 조회
    List<StockDto.Response> findAllStocks();

    // 재고 수량 변경 및 이력 쌓기
//    void updateStockQuantity(Long stockId, Long amount);


}
