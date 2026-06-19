package com.buyflow.erp.Service;

import java.util.Map;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Entity.Inspection;

public interface InspectionService {

    PageResponse<InspectionDto.ListResponse> getInspections(InspectionDto.SearchCondition condition);

    PageResponse<InspectionDto.Response> getPendingInspections(InspectionDto.SearchCondition condition);

    InspectionDto.Response getPendingInspectionDetail(Long receiptId);

    Inspection getInspection(Long inspectionId);

    void saveInspection(InspectionDto.CreateRequest request);

    void saveInspectionResult(Long receiptId, InspectionDto.ResultRequest request);

    InspectionDto.SummaryResponse getInspectionSummary();

    Map<String, Object> getInspectionFilterOptions();
}