package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Dto.StockDto.Response;
import com.buyflow.erp.Service.InspectionService;
import com.buyflow.erp.Service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("React 주소")
public class StockController {
    // /api/v1/stocks/history 같은 주소로 StockHistory 기능도 같이 다룰 예정...
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto.Response>> getStockList() {
        // 서비스에서 DTO가 담긴 List를 받아와서 화면으로 뿌려줌.
        List<Response> list = stockService.findAllStocks();
        return ResponseEntity.ok(list);
    }
}
