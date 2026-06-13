package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Dto.WarehouseDto.Detail;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    
    // 목록 조회
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

    	rs.setType(warehouse.getType());
        rs.setWarehouseCode(warehouse.getWarehouseCode());
        rs.setWarehouseName(warehouse.getWarehouseName());
        rs.setZipcode(warehouse.getZipcode());
        rs.setAddress(warehouse.getAddress());
        rs.setDetailAddress(warehouse.getDetailAddress());
        rs.setContact(warehouse.getContact());
        rs.setUseYn(warehouse.getUseYn());
        rs.setCreatedAt(warehouse.getCreatedAt());
        rs.setUpdatedAt(warehouse.getUpdatedAt());
        rs.setManagerName(warehouse.getUser().getUserName());

        return rs;

    }

    //한건 조회
	@Override
	public WarehouseDto.Detail getWarehouse(String warehouseCode) {
		Warehouse warehouse = warehouseRepository.findById(warehouseCode)
				.orElseThrow(() -> new RuntimeException("창고를 찾을 수 없습니다."));
		
		return convertToDetailDto(warehouse);
	}
	
	//DTO 변환 메서드
    private WarehouseDto.Detail convertToDetailDto(Warehouse warehouse) {
    	WarehouseDto.Detail detail = new WarehouseDto.Detail();

    	detail.setType(warehouse.getType());
    	detail.setWarehouseCode(warehouse.getWarehouseCode());
    	detail.setWarehouseName(warehouse.getWarehouseName());
    	detail.setZipcode(warehouse.getZipcode());
    	detail.setAddress(warehouse.getAddress());
    	detail.setDetailAddress(warehouse.getDetailAddress());
    	detail.setContact(warehouse.getContact());
    	detail.setUseYn(warehouse.getUseYn());
    	detail.setCreatedAt(warehouse.getCreatedAt());
    	detail.setUpdatedAt(warehouse.getUpdatedAt());
    	detail.setManagerName(warehouse.getUser().getUserName());

        return detail;

    }

    // 창고 삭제
	@Override
	public void deleteWarehouse(String warehouseCode) {
		Warehouse warehouse = warehouseRepository.findById(warehouseCode)
				.orElseThrow(() -> new RuntimeException("창고 없음."));
		
		warehouseRepository.delete(warehouse);
	}
	
	// 창고 등록
	@Override
	@Transactional
	public void createWarehouse(WarehouseDto.Create request) {
		
		if(warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
			throw new IllegalArgumentException (
					"이미 존재하는 창고 코드입니다: " + request.getWarehouseCode()
			);
		}
		
    	Warehouse warehouse = new Warehouse();
    	
    	warehouse.setType(request.getType());
		warehouse.setWarehouseCode(request.getWarehouseCode());
		warehouse.setWarehouseName(request.getWarehouseName());
		warehouse.setZipcode(request.getZipcode());
		warehouse.setAddress(request.getAddress());
		warehouse.setDetailAddress(request.getDetailAddress());
		warehouse.setContact(request.getContact());
		warehouse.setUseYn(request.getUseYn());
		
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
		
		warehouse.setUser(user);
		
		warehouseRepository.save(warehouse);

	}
	
	// 창고 수정(patch)
	@Override
	public void updateWarehouse(String warehouseCode, WarehouseDto.Update request) {
		Warehouse warehouse = warehouseRepository.findById(warehouseCode)
				.orElseThrow(() -> new RuntimeException("창고 없음."));
		
		if(request.getWarehouseName() != null) {
			warehouse.setWarehouseName(request.getWarehouseName());
		}
		
		if(request.getZipcode() != null) {
			warehouse.setZipcode(request.getZipcode());
		}
		
		if(request.getAddress() != null) {
			warehouse.setAddress(request.getAddress());
		}
		
		if(request.getDetailAddress() != null) {
			warehouse.setDetailAddress(request.getDetailAddress());
		}
		
		if(request.getContact() != null) {
			warehouse.setContact(request.getContact());
		}
		
		if(request.getUseYn() != null) {
			warehouse.setUseYn(request.getUseYn());
		}
		
		if(request.getType() != null) {
			warehouse.setType(request.getType());
		}
		
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("사용자 없음."));
		
		warehouse.setUser(user);
		
		warehouseRepository.save(warehouse);
	}
}
