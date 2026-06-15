package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Service.WarehouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseDto.HouseList>> getWarehouseList() {
        List<WarehouseDto.HouseList> list = warehouseService.findAllWarehouses();
        return ResponseEntity.ok(list);
    }
}