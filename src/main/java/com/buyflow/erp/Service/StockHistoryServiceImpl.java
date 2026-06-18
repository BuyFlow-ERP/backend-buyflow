package com.buyflow.erp.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.StockHistoryRepository;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockHistoryServiceImpl
        implements StockHistoryService {

    private final StockHistoryRepository stockHistoryRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<StockHistoryResponseDto> getStockHistory() {

        return stockHistoryRepository
                .findAllByOrderByHistoryIdDesc()
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<StockHistoryResponseDto> getStockHistoryByType(
            String historyType) {

        return stockHistoryRepository
                .findByHistoryTypeOrderByHistoryIdDesc(historyType)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public StockHistoryResponseDto getStockHistory(
            Long historyId) {

        return convert(
                stockHistoryRepository
                        .findById(historyId)
                        .orElseThrow());
    }

    private StockHistoryResponseDto convert(
            StockHistory history) {

        Stock stock = stockRepository
                .findById(history.getStockId())
                .orElse(null);

        var product = stock != null
                ? productRepository
                        .findById(stock.getProductId())
                        .orElse(null)
                : null;

        var warehouse = stock != null
                ? warehouseRepository
                        .findById(stock.getWarehouseCode())
                        .orElse(null)
                : null;

        return StockHistoryResponseDto.builder()
                .id(history.getHistoryId())
                .occurredAt(
                        history.getCreatedAt() != null
                                ? history.getCreatedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                : "")
                .movementType(history.getHistoryType())
                .itemCode(
                        product != null
                                ? product.getProductNo()
                                : "")
                .itemName(
                        product != null
                                ? product.getProductName()
                                : "")
                .warehouseName(
                        warehouse != null
                                ? warehouse.getWarehouseName()
                                : "")
                .quantity(history.getChangeQty())
                .beforeStock(history.getBeforeQty())
                .afterStock(history.getAfterQty())
                .referenceNumber("")
                .reason(history.getReason())
                .processedBy(history.getCreatedBy())
                .build();
    }
}