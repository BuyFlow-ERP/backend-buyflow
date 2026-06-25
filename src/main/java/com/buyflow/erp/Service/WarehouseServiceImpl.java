package com.buyflow.erp.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Exception.ResourceNotFoundException;
import com.buyflow.erp.Repository.UserRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

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
    @Transactional(readOnly = true)
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
    	String managerName = (condition.getManagerName() == null || condition.getManagerName().isEmpty()) ? null : condition.getManagerName();
    	
        // 레포지토리 호출
        Page<Warehouse> warehousePage = warehouseRepository.searchByFlexibleCondition(name, type, useYn, managerName, pageable);

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
        Warehouse warehouse = warehouseRepository.findByWarehouseCodeWithUser(warehouseCode)
                .orElseThrow(() -> new ResourceNotFoundException("해당 창고를 찾을 수 없습니다. (코드: " + warehouseCode + ")"));
        
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
        detail.setMemo(warehouse.getMemo());
        
        if (warehouse.getUser() != null && warehouse.getUser().getUserName() != null) {
        	detail.setManagerName(warehouse.getUser().getUserName());
        } else {
        	detail.setManagerName("-");
        }
        
        return detail;
    }

    @Override
    @Transactional
    public WarehouseDto.Create createWarehouse(WarehouseDto.Create request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 데이터가 비어있습니다.");
        }

        String inputCode = request.getWarehouseCode() != null ? request.getWarehouseCode().trim().toUpperCase() : "";
        if (inputCode.isEmpty()) {
            throw new RuntimeException("창고 코드는 필수 입력 항목입니다.");
        }
        
        if (warehouseRepository.existsById(inputCode)) {
            throw new RuntimeException("이미 존재하는 창고 코드입니다: " + inputCode);
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(inputCode);
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setZipcode(request.getZipcode());
        warehouse.setAddress(request.getAddress());
        warehouse.setDetailAddress(request.getDetailAddress());
        warehouse.setContact(request.getContact());
        warehouse.setUseYn(request.getUseYn() != null ? request.getUseYn() : "Y");
        warehouse.setType(request.getType());
        warehouse.setMemo(request.getMemo());
        
        LocalDateTime now = LocalDateTime.now();
        warehouse.setCreatedAt(now);
        warehouse.setUpdatedAt(now);
        
        String searchUid = (request.getUserId() != null) ? request.getUserId().trim() : "";
        String searchMnm = (request.getManagerName() != null) ? request.getManagerName().trim() : "";
        
        Users targetUser = null;
        
        if (!searchUid.isEmpty() && !searchUid.equalsIgnoreCase("null") && !searchUid.equalsIgnoreCase("undefined")) {
            targetUser = userRepository.findByLoginId(searchUid).orElse(null);
        }
        
        final String finalMnm = searchMnm;
        if (targetUser == null && !finalMnm.isEmpty() && !finalMnm.equals("-")) {
            targetUser = userRepository.findAll().stream()
                    .filter(u -> u.getUserName() != null && 
                            (u.getUserName().trim().contains(finalMnm) || finalMnm.contains(u.getUserName().trim())))
                    .findFirst()
                    .orElse(null);
        }
        
        if (targetUser == null) {
            targetUser = userRepository.findAll().stream().findFirst().orElse(null);
        }
        
        if (targetUser != null) {
            warehouse.setUser(targetUser);
        }
        
        Warehouse savedWarehouse = warehouseRepository.saveAndFlush(warehouse);
        
        request.setWarehouseCode(savedWarehouse.getWarehouseCode());
        if (savedWarehouse.getUser() != null) {
            request.setManagerName(savedWarehouse.getUser().getUserName());
        } else {
            request.setManagerName("-");
        }
        
        return request;
    }

    @Override
    @Transactional
    public WarehouseDto.Detail updateWarehouse(String warehouseCode, WarehouseDto.Update request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseCode)
                .orElseThrow(() -> new RuntimeException("창고 없음."));
        
        if (request.getWarehouseName() != null) warehouse.setWarehouseName(request.getWarehouseName());
        if (request.getZipcode() != null) warehouse.setZipcode(request.getZipcode());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());
        if (request.getDetailAddress() != null) warehouse.setDetailAddress(request.getDetailAddress());
        if (request.getContact() != null) warehouse.setContact(request.getContact());
        if (request.getUseYn() != null) warehouse.setUseYn(request.getUseYn());
        if (request.getType() != null) warehouse.setType(request.getType());
        if (request.getMemo() != null) warehouse.setMemo(request.getMemo());
        
        if (request.getUserId() != null && !request.getUserId().trim().isEmpty() && !request.getUserId().equals("null")) {
            Users user = userRepository.findByLoginId(request.getUserId().trim())
                    .orElse(null);
        if (user != null) {
            warehouse.setUser(user);
        }    
    }
        warehouse.setUpdatedAt(LocalDateTime.now());
        Warehouse updatedWarehouse = warehouseRepository.saveAndFlush(warehouse);        
        return getWarehouse(updatedWarehouse.getWarehouseCode());
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
        
        if (warehouse.getUser() != null && warehouse.getUser().getUserName() != null) {
        	rs.setManagerName(warehouse.getUser().getUserName());
        } else {
        	rs.setManagerName("-");
        }
        
        return rs;
    }
}