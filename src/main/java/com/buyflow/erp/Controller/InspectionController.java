package com.buyflow.erp.Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Service.InspectionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping("/pending")
    public ResponseEntity<PageResponse<InspectionDto.ListResponse>> getInspections(
        InspectionDto.SearchCondition condition) {
            return ResponseEntity.ok(inspectionService.getInspections(condition));
    }
    
    @GetMapping("/pending/filter-options")
    public ResponseEntity<Map<String, Object>> getInspectionFilterOptions() {
    	Map<String, Object> options = new HashMap<>();
    	options.put("suppliers", Arrays.asList("전체 공급업체"));
    	options.put("warehouses", Arrays.asList("전체 창고"));
    	options.put("priorities", Arrays.asList("전체", "일반", "긴급"));
    	return ResponseEntity.ok(options);
    }
    
    @GetMapping("/pending/summary")
    public ResponseEntity<Map<String, Object>> getPendingSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", 0);
        summary.put("receivedToday", 0);
        summary.put("urgent", 0);
        summary.put("overdue", 0);
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<String> saveInspection(
        @RequestBody InspectionDto.CreateRequest request) {

            inspectionService.saveInspection(request);

            return ResponseEntity.ok("검수 완료");
        }
    
    @PostMapping("/{receipId}/result")
    public ResponseEntity<String> saveInspectionResult(
    		@PathVariable(name="receiptId") Long receiptId,
    		@RequestBody InspectionDto.ResultRequest request) {
    	
    	inspectionService.saveInspectionResult(receiptId, request);
    	
    	return ResponseEntity.ok("검수 완료");
    }
    
    @GetMapping("/summary")
    public ResponseEntity<InspectionDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(inspectionService.getInspectionSummary());
    }
    
    @GetMapping("/{inspectionId}")
    public Inspection getInspections(
        @PathVariable(name="inspectionId") Long inspectionId) {

            return inspectionService.getInspection(inspectionId);
        }
    
}