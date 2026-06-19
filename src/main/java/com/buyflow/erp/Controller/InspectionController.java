package com.buyflow.erp.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Service.InspectionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping("/pending")
    public ResponseEntity<PageResponse<InspectionDto.Response>> getPendingInspections(
            InspectionDto.SearchCondition condition) {
        return ResponseEntity.ok(inspectionService.getPendingInspections(condition));
    }

    @GetMapping("/pending/filter-options")
    public ResponseEntity<Map<String, Object>> getInspectionFilterOptions() {
        return ResponseEntity.ok(inspectionService.getInspectionFilterOptions());
    }

    @GetMapping("/pending/summary")
    public ResponseEntity<InspectionDto.SummaryResponse> getPendingSummary() {
    return ResponseEntity.ok(inspectionService.getInspectionSummary());
}

    @GetMapping("/{receiptId}")
    public ResponseEntity<InspectionDto.Response> getInspectionDetail(
            @PathVariable Long receiptId) {
        return ResponseEntity.ok(inspectionService.getPendingInspectionDetail(receiptId));
    }

    @PostMapping("/{receiptId}/result")
    public ResponseEntity<String> saveInspectionResult(
            @PathVariable Long receiptId,
            @RequestBody InspectionDto.ResultRequest request) {

        inspectionService.saveInspectionResult(receiptId, request);
        return ResponseEntity.ok("검수 완료");
    }
}