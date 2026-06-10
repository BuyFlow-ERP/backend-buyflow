package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.InspectionDto;

public interface InspectionService {

    List<InspectionDto.ListResponse> findAllInspections();
    
}
