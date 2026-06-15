package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Entity.Users; 
import com.buyflow.erp.Repository.WarehouseRepository;
import com.buyflow.erp.Repository.UserRepository; 
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository; 

    // 목록 전체 조회
    @Override
    public List<WarehouseDto.HouseList> findAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return warehouses.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    
    @Override
	public PageResponse<WarehouseDto.HouseList> searchWarehouses(WarehouseDto.SearchCondition condition) {
        // 안전한 페이지 번호와 사이즈 가공
        int safePage = Math.max(condition.getPage(), 0);
        int safeSize = Math.max(condition.getSize(), 1);

        // 스프링이 제공하는 PageRequest 객체 생성
        Pageable pageable = PageRequest.of(safePage, safeSize);
    
    	// 빈 문자열("")이나 화면에서 넘어온 "전체"라는 텍스트를 null로 바꿔서 쿼리에 전달합니다.
    	String name = (condition.getWarehouseName() == null || condition.getWarehouseName().isEmpty()) ? null : condition.getWarehouseName();
    	String type = (condition.getType() == null || condition.getType().isEmpty() || condition.getType().equals("전체")) ? null : condition.getType();
    	String useYn = (condition.getUseYn() == null || condition.getUseYn().isEmpty() || condition.getUseYn().equals("전체")) ? null : condition.getUseYn();

        // 레포지토리 호출
        Page<Warehouse> warehousePage = warehouseRepository.searchByFlexibleCondition(name, type, useYn, pageable);

    	// 엔티티를 DTO 목록으로 변환
    	List<WarehouseDto.HouseList> dtoList = warehousePage.getContent().stream()
        	    .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        // 기존 프로젝트 규격인 PageResponse 포맷에 맞춰 조립하여 반환
        return new PageResponse<>(
                dtoList,
                new PageResponse.Pagination(
                        warehousePage.getNumber() + 1, 
                        warehousePage.getSize(), 
                        warehousePage.getTotalElements(), 
                        warehousePage.getTotalPages()
                )
        );   
	}
    
    @Override
    public WarehouseDto.Detail getWarehouse(String warehouseCode) {
        Warehouse warehouse = warehouseRepository.findById(warehouseCode)
                .orElseThrow(() -> new RuntimeException("창고 없음."));
        
        WarehouseDto.Detail detail = new WarehouseDto.Detail();
        detail.setWarehouseCode(warehouse.getWarehouseCode());
        detail.setWarehouseName(warehouse.getWarehouseName());
        detail.setZipcode(warehouse.getZipcode());
        detail.setAddress(warehouse.getAddress());
        detail.setDetailAddress(warehouse.getDetailAddress());
        detail.setContact(warehouse.getContact());
        detail.setUseYn(warehouse.getUseYn());
        detail.setCreatedAt(warehouse.getCreatedAt());
        detail.setUpdatedAt(warehouse.getUpdatedAt());
        detail.setType(warehouse.getType());
        if (warehouse.getUser() != null) {
            detail.setManagerName(warehouse.getUser().getUserName()); // 필드명은 상황에 맞게 수정
        }
        return detail;
    }
    
    @Override
    @Transactional
    public void createWarehouse(WarehouseDto.Create request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(request.getWarehouseCode());
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setZipcode(request.getZipcode());
        warehouse.setAddress(request.getAddress());
        warehouse.setDetailAddress(request.getDetailAddress());
        warehouse.setContact(request.getContact());
        warehouse.setUseYn(request.getUseYn());
        warehouse.setType(request.getType());
        
        // request.getUserId()가 올바르게 동작합니다 (DTO에 추가했기 때문)
        if (request.getUserId() != null) {
            Users user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
            warehouse.setUser(user);
        }
        
        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void updateWarehouse(String warehouseCode, WarehouseDto.Update request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseCode)
                .orElseThrow(() -> new RuntimeException("창고 없음."));
        
        if (request.getWarehouseName() != null) warehouse.setWarehouseName(request.getWarehouseName());
        if (request.getZipcode() != null) warehouse.setZipcode(request.getZipcode());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());
        if (request.getDetailAddress() != null) warehouse.setDetailAddress(request.getDetailAddress());
        if (request.getContact() != null) warehouse.setContact(request.getContact());
        if (request.getUseYn() != null) warehouse.setUseYn(request.getUseYn());
        if (request.getType() != null) warehouse.setType(request.getType());
        
        if (request.getUserId() != null) {
            Users user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
            warehouse.setUser(user);
        }
        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(String warehouseCode) {
        Warehouse warehouse = warehouseRepository.findById(warehouseCode)
                .orElseThrow(() -> new RuntimeException("창고 없음."));
        warehouseRepository.delete(warehouse);
    }
    
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
        if (warehouse.getUser() != null) {
            rs.setManagerName(warehouse.getUser().getUserName()); // 필요시 엔티티 이름 필드 매핑
        }
        return rs;
    }
}