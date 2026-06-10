package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Dto.StockDto.Response;
import com.buyflow.erp.Service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto.Response>> getStockList() {
        List<Response> list = stockService.findAllStocks();
        return ResponseEntity.ok(list);
    }
}
