package com.buyflow.erp.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Repository.SupplierRepository;
import com.buyflow.erp.Service.PurchaseOrderService;
import com.buyflow.erp.Service.PurchaseRequestService;
import com.buyflow.erp.Service.WarehouseService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({"/orders", "/api/orders"})
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;
    private final WarehouseService warehouseService;
    private final PurchaseRequestService purchaseRequestService;
    private final SupplierRepository supplierRepository;

    @GetMapping("/form-options")
    public ResponseEntity<Map<String, Object>> getFormOptions() {
        Map<String, Object> options = new HashMap<>();
        
        options.put("statuses", Arrays.asList("전체", "PENDING", "APPROVED", "CANCELLED"));
        
        // 🚀 [하드코딩 파괴 ➔ DB 동적 연동]
        // 1. DB에서 활성화되어 있거나 전체 공급업체 엔티티 리스트를 긁어옵니다.
        // (※ 팀원들이 만든 레포지토리 메서드명이 findAll() 또는 findAllByUseYn("Y") 등인지 확인!)
        List<Supplier> actualSuppliers = supplierRepository.findAll(); 
        
        // 2. 프론트엔드가 요구하는 단순 문자열 배열(String[]) 규격에 맞춰 이름(SupplierName)만 쏙 추출합니다!
        List<String> dynamicSupplierNames = actualSuppliers.stream()
                .map(supplier -> supplier.getSupplierName()) // 엔티티의 공급업체명 Getter 호출
                .filter(name -> name != null && !name.trim().isEmpty()) // 혹시 모를 빈값 필터링
                .collect(Collectors.toList());
        
        // 3. 만약 DB에 데이터가 하나도 없다면 화면이 깨지지 않게 최소한의 방어선 가드레일만 구축
        if (dynamicSupplierNames.isEmpty()) {
            dynamicSupplierNames = Arrays.asList("동양산업", "대한기계상사", "세진테크");
        }
        
        // 4. 주머니에 하드코딩 대신 찐 DB에서 가져온 따끈따끈한 이름 리스트를 적재합니다!
        options.put("suppliers", dynamicSupplierNames);
        
        // 구매 요청 목록 및 입고 창고 기존 로직 유지
        List<PurchaseRequestDto.ListResponse> approvedRequests = 
                purchaseRequestService.getApprovedRequestsWithoutPaging();
        options.put("approvedPurchaseRequests", approvedRequests);

        List<WarehouseDto.HouseList> actualWarehouses = warehouseService.findAllWarehouses().stream()
                .filter(w -> "Y".equals(w.getUseYn()))
                .collect(Collectors.toList());
        options.put("warehouses", actualWarehouses);
        
        return ResponseEntity.ok(options);
    }
    
    @GetMapping("/purchase-requests/{requestId}/items")
    public ResponseEntity<List<PurchaseOrderDto.ItemResponse>> getRequestItems(
    		@PathVariable(name = "requestId") Long requestId) {
    	List<PurchaseOrderDto.ItemResponse> items = service.getApprovedRequestItems(requestId);
    	
    	return ResponseEntity.ok(items);
    }
    
    // 1. 발주 단건 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto.Response> getOrder(
    		@PathVariable(name= "orderId") Long orderId) { 
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
    
 // PurchaseOrderController.java 내부의 getOrderFilterOptions 메서드 최종 교체

    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, List<String>>> getOrderFilterOptions() {
        
        Map<String, List<String>> options = new HashMap<>();
        options.put("statuses", Arrays.asList("전체", "PENDING", "APPROVED", "CANCELLED"));
        options.put("suppliers", Arrays.asList("전체", "동양산업", "대한기계상사", "세진테크"));
        
        return ResponseEntity.ok(options);
    }
    
//    private Map<String, String> createOption(String value, String label) {
//    	Map<String, String> option = new HashMap<>();
//    	option.put("value", value);
//    	option.put("label", label);
//    	return option;
//    }

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
            @PathVariable(name = "orderId") Long orderId,
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