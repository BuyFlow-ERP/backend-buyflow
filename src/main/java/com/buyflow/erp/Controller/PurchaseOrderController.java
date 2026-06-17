package com.buyflow.erp.Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseOrderDto;
import com.buyflow.erp.Service.PurchaseOrderService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000") // React 연동을 위한 크로스 오리진 설정
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    // 1. 발주 단건 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto.Response> getOrder(@PathVariable Long orderId) {
        PurchaseOrderDto.Response response = service.getOrderWithItems(orderId);
        return ResponseEntity.ok(response);
    }

    // 2. 발주 목록 조회
    @GetMapping
    public ResponseEntity<PageResponse<PurchaseOrderDto.Response>> getOrderList(
        PurchaseOrderDto.SearchCondition condition) {
        
        PageResponse<PurchaseOrderDto.Response> response = service.getOrderList(condition);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getOrderFilterOptions() {
    	Map<String, List<Map<String, String>>> options = new HashMap<>();
    	List<Map<String, String>> statusOptions = Arrays.asList(
    		createOption("전체", "전체"),
    		createOption("PENDING", "대기 중"),
    		createOption("APPROVED", "승인 완료"),
    		createOption("CANCELLED", "발주 취소")
    	);
    	
    	options.put("orderStatuses", statusOptions);
    	return ResponseEntity.ok(options);
    }
    
    private Map<String, String> createOption(String value, String label) {
    	Map<String, String> option = new HashMap<>();
    	option.put("value", value);
    	option.put("label", label);
    	return option;
    }

    // 3. 발주 등록
    @PostMapping
    public ResponseEntity<PurchaseOrderDto.Response> createOrder(@RequestBody PurchaseOrderDto.Request request) {
        // 서비스 내부에서 변환 작업까지 끝낸 DTO를 받아와 바로 리턴합니다.
        PurchaseOrderDto.Response response = service.createOrder(request);
        return ResponseEntity.ok(response);
    }

    // 4. 발주 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto.Response> updateOrder(
            @PathVariable Long orderId,
            @RequestBody PurchaseOrderDto.Request request) {

        PurchaseOrderDto.Response response = service.updateOrder(orderId, request);
        return ResponseEntity.ok(response);
    }

    // 5. 발주 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        service.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}