package com.buyflow.erp.Controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InventoryAdjustmentRequest;
import com.buyflow.erp.Dto.InventoryAdjustmentResponse;
import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Dto.StockListResponse;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.WarehouseRepository;
import com.buyflow.erp.Service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping({ "/stocks", "/inventories" })
public class StockController {

        private final StockRepository stockRepository;
        private final ProductRepository productRepository;
        private final WarehouseRepository warehouseRepository;
        private final InventoryService inventoryService;

        @PostMapping("/{stockId}/adjustments")
        public ResponseEntity<InventoryAdjustmentResponse> adjustStock(
                        @PathVariable(name = "stockId") Long stockId,
                        @RequestBody InventoryAdjustmentRequest request) {

                return ResponseEntity.ok(
                                inventoryService.adjustStock(stockId, request));
        }

        @GetMapping
        public StockListResponse getInventories(
                        @RequestParam(name = "itemCode", required = false) String itemCode,
                        @RequestParam(name = "itemName", required = false) String itemName,
                        @RequestParam(name = "category", required = false) String category,
                        @RequestParam(name = "warehouseCode", required = false) String warehouseCode,
                        @RequestParam(name = "stockStatus", required = false) String stockStatus) {

                List<StockDto> items = stockRepository.findAll()
                                .stream()
                                .map(this::convert)

                                .filter(item -> itemCode == null
                                                || itemCode.isBlank()
                                                || item.getItemCode().contains(itemCode))

                                .filter(item -> itemName == null
                                                || itemName.isBlank()
                                                || item.getItemName().contains(itemName))

                                .filter(item -> category == null
                                                || category.isBlank()
                                                || category.equals("전체")
                                                || category.equals(item.getCategory()))

                                .filter(item -> warehouseCode == null
                                                || warehouseCode.isBlank()
                                                || warehouseCode.equals("전체")
                                                || warehouseCode.equals(item.getWarehouseCode()))

                                .filter(item -> {

                                        if (stockStatus == null
                                                        || stockStatus.isBlank()
                                                        || stockStatus.equals("전체")) {
                                                return true;
                                        }

                                        if (stockStatus.equals("재고 없음")) {
                                                return item.getCurrentStock() <= 0;
                                        }

                                        if (stockStatus.equals("안전재고 미만")) {
                                                return item.getCurrentStock() > 0
                                                                && item.getCurrentStock() < item.getSafetyStock();
                                        }

                                        if (stockStatus.equals("정상")) {
                                                return item.getCurrentStock() >= item.getSafetyStock();
                                        }

                                        return true;
                                })

                                .collect(Collectors.toList());

                long normal = items.stream()
                                .filter(item -> item.getCurrentStock() > 0
                                                && item.getCurrentStock() >= item.getSafetyStock())
                                .count();

                long low = items.stream()
                                .filter(item -> item.getCurrentStock() > 0
                                                && item.getCurrentStock() < item.getSafetyStock())
                                .count();

                long outOfStock = items.stream()
                                .filter(item -> item.getCurrentStock() <= 0)
                                .count();

                return StockListResponse.builder()
                                .items(items)
                                .summary(
                                                StockListResponse.Summary.builder()
                                                                .total(items.size())
                                                                .normal((int) normal)
                                                                .low((int) low)
                                                                .outOfStock((int) outOfStock)
                                                                .build())
                                .pagination(
                                                StockListResponse.Pagination.builder()
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

                List<Map<String, String>> warehouses = new ArrayList<>();

                warehouses.add(
                                Map.of(
                                                "value", "전체",
                                                "label", "전체"));

                warehouseRepository.findAll()
                                .forEach(warehouse -> {
                                        warehouses.add(
                                                        Map.of(
                                                                        "value", warehouse.getWarehouseCode(),
                                                                        "label", warehouse.getWarehouseName()));
                                });

                result.put("warehouses", warehouses);

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
                                                "INSPECTION_ADJUST",
                                                "UPDATE",
                                                "DELETE",
                                                "CANCEL"));

                return result;
        }

        private StockDto convert(Stock stock) {

                var product = productRepository
                                .findById(stock.getProductId())
                                .orElse(null);

                var warehouse = warehouseRepository
                                .findById(stock.getWarehouseCode())
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
                                .currentStock(stock.getQuantity())
                                .safetyStock(
                                                stock.getSafetyStock() != null
                                                                ? stock.getSafetyStock()
                                                                : 0)
                                .lastChangedAt(
                                                stock.getUpdatedAt() != null
                                                                ? stock.getUpdatedAt()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "yyyy-MM-dd HH:mm"))
                                                                : "")
                                .build();
        }
}