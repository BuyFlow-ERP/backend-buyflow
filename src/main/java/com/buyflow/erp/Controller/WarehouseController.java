package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Service.WarehouseService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins= "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<WarehouseDto.HouseList>> getWarehouseList() {
        List<WarehouseDto.HouseList> list = warehouseService.findAllWarehouses();
        return ResponseEntity.ok(list);
    }
    
    // 단건 조회
    @GetMapping("/warehouse/{warehouseCode}")
    public ResponseEntity<WarehouseDto.Detail> getWarehouse(
    		@PathVariable String warehouseCode) {
    	return ResponseEntity.ok(warehouseService.getWarehouse(warehouseCode));
    }
    
    // 창고 등록
    @PostMapping
    public ResponseEntity<Void> createWarehouse(
    		@RequestBody WarehouseDto.Create request) {
    	
    	warehouseService.createWarehouse(request);
    	
    	return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    // 창고 수정
    @PatchMapping("/{warehouseCode}")
    public ResponseEntity<Void> updateWarehouse(
    		@PathVariable String warehouseCode, 
    		@RequestBody WarehouseDto.Update request) {
    	warehouseService.updateWarehouse(warehouseCode, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 창고 삭제
    @DeleteMapping("/warehouse/{warehouseCode}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable String warehouseCode) {
    	warehouseService.deleteWarehouse(WarehouseCode);
    	
    	return ResponseEntity.noContent().build();
    }
}
