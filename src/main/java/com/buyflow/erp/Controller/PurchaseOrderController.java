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
import org.springframework.web.bind.annotation.PatchMapping;
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
        
        // 1. DB에서 엔티티 원본을 가져옵니다.
        List<Supplier> actualSuppliers = supplierRepository.findAll(); 

        // 2. 루프를 돌며 데이터를 가공하고, 동시에 콘솔에 원본 값을 찍어봅니다.
        List<Map<String, Object>> robustSuppliers = new ArrayList<>();
        
        for (Supplier supplier : actualSuppliers) {
            Map<String, Object> map = new HashMap<>();
            map.put("supplierId", supplier.getSupplierId());
            map.put("supplierName", supplier.getSupplierName());
            map.put("manager", supplier.getManager() != null ? supplier.getManager() : "-");
            map.put("contact", supplier.getContact() != null ? supplier.getContact() : "-");
            
            robustSuppliers.add(map);
        }
        
        options.put("suppliers", robustSuppliers);

        // 기존 로직 유지
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

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<PurchaseOrderDto.Response> cancelOrder(
            @PathVariable(name = "orderId") Long orderId,
            @RequestBody Map<String, String> request) {   // cancelReason 받음
        
        String cancelReason = request.get("cancelReason");
        
        PurchaseOrderDto.Response response = service.cancelOrder(orderId, cancelReason);
        return ResponseEntity.ok(response);
    }
}