package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Repository.InspectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InspectionServiceImpl implements InspectionService {
    private final InspectionRepository inspectionRepository;
    
    @Override
    public List<InspectionDto.ListResponse> findAllInspections() {
        List<Inspection> inspection = inspectionRepository.findAll();

        return inspection.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    // DTO 변환 메서드    
    private InspectionDto.ListResponse convertToResponseDto(Inspection inspection) {
    	InspectionDto.ListResponse rs = new InspectionDto.ListResponse();

//        rs.setStockId(inspection.getStockId());
//        rs.setProductId(inspection.getProductId());
//        rs.setWarehouseCode(inspection.getWarehouseCode());
//        rs.setQuantity(inspection.getQuantity());
//        rs.setStockStatus(inspection.getStockStatus());
//        rs.setUpdatedAt(inspection.getUpdatedAt());
//
//        rs.setProductName("품목명");
//        rs.setWarehouseName("창고명");

        return rs;

    }
}
