package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.InventoryAdjustmentRequest;
import com.buyflow.erp.Dto.InventoryAdjustmentResponse;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Repository.StockHistoryRepository;
import com.buyflow.erp.Repository.StockRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    @Override
    @Transactional
    public InventoryAdjustmentResponse adjustStock(Long stockId, InventoryAdjustmentRequest request) {

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다."));

        int beforeStock = stock.getQuantity();

        int qty = request.getQuantity() != null ? request.getQuantity() : 0;

        int adjustedQty;

        if ("INCREASE".equalsIgnoreCase(request.getAdjustmentType())) {
            adjustedQty = qty;
        } else if ("DECREASE".equalsIgnoreCase(request.getAdjustmentType())) {
            adjustedQty = -qty;
        } else {
            throw new RuntimeException("잘못된 조정 타입입니다.");
        }

        int afterStock = beforeStock + adjustedQty;

        if (afterStock < 0) {
            throw new RuntimeException("재고는 0보다 작을 수 없습니다.");
        }

        // 1. 재고 업데이트
        stock.setQuantity(afterStock);
        stockRepository.save(stock);

        // 2. 히스토리 저장
        StockHistory history = new StockHistory();

        history.setStockId(stockId);
        history.setBeforeQty((long) beforeStock);
        history.setAfterQty((long) afterStock);
        history.setChangeQty((long) adjustedQty);
        history.setReason(request.getReason());
        history.setHistoryType("INSPECTION_ADJUST");
        history.setCreatedAt(LocalDateTime.now());
        history.setCreatedBy("SYSTEM");

        stockHistoryRepository.save(history);

        // 3. 응답 반환
        return InventoryAdjustmentResponse.builder()
                .stockId(stockId)
                .beforeStock(beforeStock)
                .afterStock(afterStock)
                .adjustedQuantity(adjustedQty)
                .movementType("INSPECTION_ADJUST")
                .reason(request.getReason())
                .build();
    }
}