package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    
    @Override
    public List<WarehouseDto.HouseList> findAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return warehouses.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    // DTO 변환 메서드    
    private WarehouseDto.HouseList convertToResponseDto(Warehouse warehouse) {
    	WarehouseDto.HouseList rs = new WarehouseDto.HouseList();

//        rs.setStockId(warehouse.getStockId());
//        rs.setProductId(warehouse.getProductId());
//        rs.setWarehouseCode(warehouse.getWarehouseCode());
//        rs.setQuantity(warehouse.getQuantity());
//        rs.setStockStatus(warehouse.getStockStatus());
//        rs.setUpdatedAt(warehouse.getUpdatedAt());
//
//        rs.setProductName("품목명");
//        rs.setWarehouseName("창고명");

        return rs;

    }
}
