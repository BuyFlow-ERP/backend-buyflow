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
public List<StockHistoryResponseDto> getStockHistory(
        String fromDate,
        String toDate,
        String itemKeyword,
        String warehouseCode,
        String movementType) {

    return stockHistoryRepository
            .findAllByOrderByHistoryIdDesc()
            .stream()

            .filter(history -> {

                Stock stock = stockRepository
                        .findById(history.getStockId())
                        .orElse(null);

                if (stock == null) {
                    return false;
                }

                var product = productRepository
                        .findById(stock.getProductId())
                        .orElse(null);

                String itemCode =
                        product != null
                                ? product.getProductNo()
                                : "";

                String itemName =
                        product != null
                                ? product.getProductName()
                                : "";

                String historyDate =
                        history.getCreatedAt() != null
                                ? history.getCreatedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                : "";

                boolean matchMovement =
                        movementType == null
                                || movementType.isBlank()
                                || movementType.equals("전체")
                                || movementType.equals(history.getHistoryType());

                boolean matchWarehouse =
                        warehouseCode == null
                                || warehouseCode.isBlank()
                                || warehouseCode.equals("전체")
                                || warehouseCode.equals(stock.getWarehouseCode());

                boolean matchKeyword =
                        itemKeyword == null
                                || itemKeyword.isBlank()
                                || itemCode.contains(itemKeyword)
                                || itemName.contains(itemKeyword);

                boolean matchFromDate =
                        fromDate == null
                                || fromDate.isBlank()
                                || historyDate.compareTo(fromDate) >= 0;

                boolean matchToDate =
                        toDate == null
                                || toDate.isBlank()
                                || historyDate.compareTo(toDate) <= 0;

                return matchMovement
                        && matchWarehouse
                        && matchKeyword
                        && matchFromDate
                        && matchToDate;
            })

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