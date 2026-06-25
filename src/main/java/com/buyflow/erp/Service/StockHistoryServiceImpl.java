package com.buyflow.erp.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Entity.Warehouse;
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
                                .map(this::convertToDto)
                                .toList();
        }

        @Override
        public List<StockHistoryResponseDto> getStockHistoryByType(
                        String historyType) {

                return stockHistoryRepository
                                .findByHistoryTypeOrderByHistoryIdDesc(
                                                historyType)
                                .stream()
                                .map(this::convertToDto)
                                .toList();
        }

        @Override
        public List<StockHistoryResponseDto> searchStockHistory(
                        String fromDate,
                        String toDate,
                        String itemKeyword,
                        String warehouseCode,
                        String movementType) {

                return stockHistoryRepository
                                .findAllByOrderByHistoryIdDesc()
                                .stream()
                                .map(this::convertToDto)

                                .filter(dto -> movementType == null
                                                || movementType.isBlank()
                                                || "전체".equals(movementType)
                                                || movementType.equals(dto.getMovementType()))

                                .filter(dto -> warehouseCode == null
                                                || warehouseCode.isBlank()
                                                || "전체".equals(warehouseCode)
                                                || warehouseCode.equals(dto.getWarehouseName()))

                                .filter(dto -> itemKeyword == null
                                                || itemKeyword.isBlank()
                                                || (dto.getItemName() != null
                                                                && dto.getItemName().contains(itemKeyword))
                                                || (dto.getItemCode() != null
                                                                && dto.getItemCode().contains(itemKeyword)))

                                .toList();
        }

        @Override
        public StockHistoryResponseDto getStockHistory(
                        Long historyId) {

                return convertToDto(
                                stockHistoryRepository
                                                .findById(historyId)
                                                .orElseThrow());
        }

        private StockHistoryResponseDto convertToDto(
                        StockHistory history) {

                StockHistoryResponseDto dto = new StockHistoryResponseDto();

                dto.setHistoryId(history.getHistoryId());

                dto.setOccurredAt(
                                history.getCreatedAt() == null
                                                ? null
                                                : history.getCreatedAt().toString());

                dto.setMovementType(history.getHistoryType());

                dto.setQuantity(history.getChangeQty());
                dto.setBeforeStock(history.getBeforeQty());
                dto.setAfterStock(history.getAfterQty());

                dto.setReason(history.getReason());
                dto.setProcessedBy(history.getCreatedBy());

                if (history.getRelatedReceiptItemId() != null) {
                        dto.setReferenceNumber(
                                        String.valueOf(
                                                        history.getRelatedReceiptItemId()));
                } else if (history.getRelatedOrderItemId() != null) {
                        dto.setReferenceNumber(
                                        String.valueOf(
                                                        history.getRelatedOrderItemId()));
                }

                Stock stock = stockRepository
                                .findById(history.getStockId())
                                .orElse(null);

                if (stock != null) {

                        Product product = productRepository
                                        .findById(stock.getProductId())
                                        .orElse(null);

                        Warehouse warehouse = warehouseRepository
                                        .findById(stock.getWarehouseCode())
                                        .orElse(null);

                        if (product != null) {
                                dto.setItemCode(product.getProductNo());
                                dto.setItemName(product.getProductName());
                        }

                        if (warehouse != null) {
                                dto.setWarehouseName(
                                                warehouse.getWarehouseName());
                        }
                }

                return dto;
        }
}
