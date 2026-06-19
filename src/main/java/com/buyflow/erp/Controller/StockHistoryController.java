package com.buyflow.erp.Controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Repository.WarehouseRepository;
import com.buyflow.erp.Service.StockHistoryService;

import lombok.RequiredArgsConstructor;
import java.util.Map;
import com.buyflow.erp.Repository.WarehouseRepository;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;
    private final WarehouseRepository warehouseRepository;

   @GetMapping
public List<StockHistoryResponseDto> getStockHistory(
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate,
        @RequestParam(required = false) String itemKeyword,
        @RequestParam(required = false) String warehouseCode,
        @RequestParam(required = false) String movementType) {

    return stockHistoryService.getStockHistory(
            fromDate,
            toDate,
            itemKeyword,
            warehouseCode,
            movementType);
}

    @GetMapping("/type/{historyType}")
    public List<StockHistoryResponseDto> getStockHistoryByType(
            @PathVariable String historyType) {

        return stockHistoryService
                .getStockHistoryByType(historyType);
    }

    @GetMapping("/{historyId}")
    public StockHistoryResponseDto getStockHistory(
            @PathVariable Long historyId) {

        return stockHistoryService
                .getStockHistory(historyId);
    }

    @GetMapping("/filter-options")
    public Map<String, Object> getFilterOptions() {

        Map<String, Object> result = new HashMap<>();

        result.put(
                "warehouses",
                warehouseRepository.findAll()
                        .stream()
                        .map(warehouse -> Map.of(
                                "value", warehouse.getWarehouseCode(),
                                "label", warehouse.getWarehouseName()))
                        .toList());

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
}