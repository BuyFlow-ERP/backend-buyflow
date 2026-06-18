package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.StockHistoryResponseDto;
import com.buyflow.erp.Service.StockHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    @GetMapping
    public List<StockHistoryResponseDto> getStockHistory() {

        return stockHistoryService.getStockHistory();
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
}