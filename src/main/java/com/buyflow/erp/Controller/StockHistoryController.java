package com.buyflow.erp.Controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Service.StockHistoryService;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;
    private final WarehouseRepository warehouseRepository;

<<<<<<< HEAD
   @GetMapping
public List<StockHistoryResponseDto> getStockHistory(
        @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(required = false) String itemKeyword,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String movementType) {

        System.out.println(
                "itemKeyword = " + itemKeyword);

        return stockHistoryService.searchStockHistory(
                fromDate,
                toDate,
                itemKeyword,
                warehouseCode,
                movementType);
    }

    @GetMapping("/type/{historyType}")
    public List<StockHistoryResponseDto> getStockHistoryByType(
            @PathVariable String historyType) {
>>>>>>> b235d02 (feat: connect stock history to database)

        return stockHistoryService
                .getStockHistoryByType(historyType);

    @GetMapping("/{historyId}")
    public StockHistoryResponseDto getStockHistory(
            @PathVariable(name = "historyId") Long historyId) {

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
                        "RECEIPT",
                        "UPDATE",
                        "DELETE",
                        "CANCEL"));

        return result;
    }
}