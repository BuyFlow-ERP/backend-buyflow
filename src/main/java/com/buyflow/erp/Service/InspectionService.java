package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Entity.Inspection;

public interface InspectionService {

    PageResponse<InspectionDto.ListResponse> getInspections(InspectionDto.SearchCondition condition);

    Inspection getInspection(Long inspectionId);

    void saveInspection(InspectionDto.CreateRequest request);
    
}
