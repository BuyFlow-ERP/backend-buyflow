package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Service.InspectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspections")
public class InspectionController {

    private final InspectionService inspectionService;
    
    @GetMapping
    public ResponseEntity<List<InspectionDto.ListResponse>> getInspectionList() {
        List<InspectionDto.ListResponse> list = inspectionService.findAllInspections();
        return ResponseEntity.ok(list);
    }
    
}
