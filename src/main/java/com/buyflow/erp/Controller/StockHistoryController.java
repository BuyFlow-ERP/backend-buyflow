package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Service.StockHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    @GetMapping
    public List<StockHistory> getStockHistory() {

        return stockHistoryService.getStockHistory();
    }

    @GetMapping("/type/{historyType}")
    public List<StockHistory> getStockHistoryByType(
            @PathVariable String historyType) {

        return stockHistoryService
                .getStockHistoryByType(historyType);
    }

    @GetMapping("/{historyId}")
    public StockHistory getStockHistory(
            @PathVariable Long historyId) {

        return stockHistoryService
                .getStockHistory(
                        historyId);
    }
}