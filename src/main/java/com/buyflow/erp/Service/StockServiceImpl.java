package com.buyflow.erp.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<StockDto> findAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDto> findStocksByProductId(Long productId) {
        return stockRepository.findByProductId(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDto> findStocksByWarehouseCode(String warehouseCode) {
        return stockRepository.findByWarehouseCode(warehouseCode)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private StockDto convertToDto(Stock stock) {

        Product product = productRepository.findById(stock.getProductId())
                .orElse(null);

        Warehouse warehouse = warehouseRepository.findById(stock.getWarehouseCode())
                .orElse(null);

        return StockDto.builder()
                .id(stock.getStockId())
                .itemId(stock.getProductId())
                .itemCode(
                        product != null
                                ? product.getProductNo()
                                : "P-" + stock.getProductId())
                .itemName(
                        product != null
                                ? product.getProductName()
                                : "")
                .category(
                        product != null
                                ? product.getCategoryName()
                                : "")
                .spec(
                        product != null
                                ? product.getSpec()
                                : "")
                .unit(
                        product != null
                                ? product.getUnit()
                                : "EA")
                .warehouseCode(stock.getWarehouseCode())
                .warehouseName(
                        warehouse != null
                                ? warehouse.getWarehouseName()
                                : "")
                .currentStock(
                        stock.getQuantity() != null
                                ? stock.getQuantity()
                                : 0)
                .safetyStock(
                        stock.getSafetyStock() != null
                                ? stock.getSafetyStock()
                                : 0)
                .lastChangedAt(
                        stock.getUpdatedAt() != null
                                ? stock.getUpdatedAt().format(DATE_TIME_FORMATTER)
                                : "")
                .build();
    }
}
