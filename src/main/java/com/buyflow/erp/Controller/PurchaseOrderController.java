package com.buyflow.erp.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseOrderDto;
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Dto.WarehouseDto;
import com.buyflow.erp.Entity.Attachment;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Repository.AttachmentRepository;
import com.buyflow.erp.Repository.SupplierRepository;
import com.buyflow.erp.Service.FileService;
import com.buyflow.erp.Service.PurchaseOrderService;
import com.buyflow.erp.Service.PurchaseRequestService;
import com.buyflow.erp.Service.WarehouseService;

import jakarta.servlet.http.HttpServletResponse;
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
    private final FileService fileService;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/form-options")
    public ResponseEntity<Map<String, Object>> getFormOptions() {
        Map<String, Object> options = new HashMap<>();
        
        options.put("statuses", Arrays.asList("전체", "ORDERED", "CONFIRMED", "CANCELLED"));
        
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
    public ResponseEntity<PurchaseOrderDto.Response> createOrder(
    		@RequestPart("data") PurchaseOrderDto.Request request,
    		@RequestPart(value = "file", required = false) MultipartFile file) throws Exception{
        
    	if (file != null && !file.isEmpty()) {
    		Attachment savedFile = fileService.uploadFile(file, request.getCreatedBy(), request.getUserName());
    		request.setAttachmentId(savedFile.getAttachmentId());
    	}
    	
    	// 서비스 내부에서 변환 작업까지 끝낸 DTO를 받아와 바로 리턴합니다.
        PurchaseOrderDto.Response response = service.createOrder(request);
        return ResponseEntity.ok(response);
    }

    // 4. 발주 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto.Response> updateOrder(
            @PathVariable(name = "orderId") Long orderId,
            @RequestPart("data") PurchaseOrderDto.Request request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
    	
    	if (file != null && !file.isEmpty()) {
    		Attachment savedFile = fileService.uploadFile(file, request.getCreatedBy(), request.getUserName());
    		request.setAttachmentId(savedFile.getAttachmentId());
    	}

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
    
    @GetMapping("/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
    	List<PurchaseOrder> orders = service.getAllOrdersForExcel();
    	
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	Sheet sheet = workbook.createSheet("발주 내역");
    	
    	Row headerRow = sheet.createRow(0);
    	headerRow.createCell(0).setCellValue("발주 번호");
        headerRow.createCell(1).setCellValue("공급업체");
        headerRow.createCell(2).setCellValue("총 금액");
        headerRow.createCell(3).setCellValue("상태");
        
        int rowNum = 1;
        for (PurchaseOrder order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderNo());
            row.createCell(1).setCellValue(order.getSupplier() != null ? order.getSupplier().getSupplierName() : "-");
            row.createCell(2).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount() : 0);
            row.createCell(3).setCellValue(order.getOrderStatus());
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"PurchaseOrders.xlsx\"");
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    @GetMapping("/attachments/download/{attachmentId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(
            @PathVariable("attachmentId") Long attachmentId) {
        try {
            // 1. DB에서 파일 정보 조회
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("파일 정보를 찾을 수 없습니다."));

            // 2. 실제 물리 파일 경로 확인
            java.nio.file.Path filePath = java.nio.file.Paths.get(attachment.getFilePath());

            if (!java.nio.file.Files.exists(filePath)) {
                throw new RuntimeException("서버에 실제 파일이 존재하지 않습니다.");
            }

            // 3. 리소스 생성 (Stream)
            org.springframework.core.io.Resource resource = 
                    new org.springframework.core.io.InputStreamResource(java.nio.file.Files.newInputStream(filePath));

            // 4. 파일명 인코딩 (한글 깨짐 방지)
            String encodedFileName = org.springframework.web.util.UriUtils.encode(attachment.getOriginalName(), java.nio.charset.StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

            // 5. 다운로드 헤더 설정 및 반환
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(java.nio.file.Files.size(filePath))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}