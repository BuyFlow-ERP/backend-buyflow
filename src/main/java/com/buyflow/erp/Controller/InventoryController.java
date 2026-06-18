package com.buyflow.erp.Controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InventoryDto;
import com.buyflow.erp.Dto.InventoryListResponse;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Repository.StockRepository;

import lombok.RequiredArgsConstructor;
import java.util.Map;

import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventories")
public class InventoryController {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public InventoryListResponse getInventories() {

        List<InventoryDto> items = stockRepository.findAll()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());

        return InventoryListResponse.builder()
                .items(items)
                .summary(
                        InventoryListResponse.Summary.builder()
                                .total(items.size())
                                .normal(items.size())
                                .low(0)
                                .outOfStock(0)
                                .build())
                .pagination(
                        InventoryListResponse.Pagination.builder()
                                .page(1)
                                .size(items.size())
                                .totalElements((long) items.size())
                                .totalPages(1)
                                .build())
                .build();
    }

    @GetMapping("/filter-options")
    public Map<String, Object> getFilterOptions() {

        Map<String, Object> result = new HashMap<>();

        result.put(
                "categories",
                List.of("전체", "기타"));

        result.put(
                "warehouses",
                List.of("전체", "WH001"));

        result.put(
                "stockStatuses",
                List.of(
                        "전체",
                        "정상",
                        "안전재고 미만",
                        "재고 없음"));
        result.put(
                "movementTypes",
                List.of(
                        "전체",
                        "INBOUND",
                        "UPDATE",
                        "DELETE",
                        "CANCEL"));

        return result;
    }

    private InventoryDto convert(Stock stock) {

        var product = productRepository
                .findById(stock.getProductId())
                .orElse(null);

        var warehouse = warehouseRepository
                .findById(stock.getWarehouseCode())
                .orElse(null);

        return InventoryDto.builder()
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
                .locationCode("-")
                .currentStock(stock.getQuantity())
                .safetyStock(
                        stock.getSafetyStock() != null
                                ? stock.getSafetyStock()
                                : 0)
                .lastChangedAt(
                        stock.getUpdatedAt() != null
                                ? stock.getUpdatedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                : "")
                .build();
    }
}