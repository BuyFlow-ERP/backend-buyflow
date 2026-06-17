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

    @GetMapping
    public ResponseEntity<PageResponse<InspectionDto.ListResponse>> getInspections(
        InspectionDto.SearchCondition condition) {
            return ResponseEntity.ok(inspectionService.getInspections(condition));
    }
    
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getInspectionFilterOptions() {
    	Map<String, List<Map<String, String>>> options = new HashMap<>();
    	List<Map<String, String>> resultOptions = Arrays.asList(
    		createOption("전체", "전체 결과"),
    		createOption("PASS", "합격"),
    		createOption("DEFECT", "불량")
    	);
    	
    	options.put("inspectionResults", resultOptions);
    	return ResponseEntity.ok(options);
    }
    
    private Map<String, String> createOption(String value, String label) {
    	Map<String, String> option = new HashMap<>();
    	option.put("value", value);
    	option.put("label", value);
    	return option;
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
    
    @PostMapping("/{receipId}/result")
    public ResponseEntity<String> saveInspectionResult(
    		@PathVariable Long receiptId,
    		@RequestBody InspectionDto.ResultRequest request) {
    	
    	inspectionService.saveInspectionResult(receiptId, request);
    	
    	return ResponseEntity.ok("검수 완료");
    }
    
    @GetMapping("/summary")
    public ResponseEntity<InspectionDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(inspectionService.getInspectionSummary());
    }
    
}