package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Service.InspectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping
    public List<Inspection> getInspections() {
        return inspectionService.getInspections();
    }

    @GetMapping("/{inspectionId}")
    public Inspection getInspections(
        @PathVariable Long inspectionId) {

            return inspectionService.getInspection(inspectionId);
        }

    @PostMapping
    public ResponseEntity<String> saveInspection(
        @RequestBody InspectionDto.CreateRequest request) {

            inspectionService.saveInspection(request);

            return ResponseEntity.ok("검수 완료");
        }
    
}