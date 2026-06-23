package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({ "/receipts", "/api/receipts" })
public class ReceiptController {

        private final ReceiptService receiptService;

        @GetMapping("/test")
        public String test() {
                return "receipt ok";
        }

        @GetMapping
        public ResponseEntity<ReceiptDto.PageResponse<ReceiptDto.ListResponse>> getReceipts(
                        @RequestParam(required = false) String activeTab,
                        @RequestParam(required = false) String orderNumber,
                        @RequestParam(required = false) String supplierKeyword,
                        @RequestParam(required = false) String itemKeyword,
                        @RequestParam(required = false) String warehouseName,
                        @RequestParam(required = false) String expectedFrom,
                        @RequestParam(required = false) String expectedTo,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                receiptService.searchReceipts(
                                                activeTab,
                                                orderNumber,
                                                supplierKeyword,
                                                itemKeyword,
                                                warehouseName,
                                                expectedFrom,
                                                expectedTo,
                                                status,
                                                page,
                                                size));
        }

        @GetMapping("/filter-options")
        public ResponseEntity<ReceiptDto.FilterOptionsResponse> getFilterOptions() {
                return ResponseEntity.ok(
                                receiptService.getFilterOptions());
        }

        @GetMapping("/form-options")
        public ResponseEntity<ReceiptDto.FormOptionsResponse> getFormOptions() {
                return ResponseEntity.ok(
                                receiptService.getFormOptions());
        }

        
        @GetMapping("/summary")
        public ResponseEntity<ReceiptDto.SummaryResponse> getSummary() {
                return ResponseEntity.ok(
                                receiptService.getSummary());
        }

        @GetMapping("/{receiptId:\\d+}")
        public ResponseEntity<ReceiptDto.DetailResponse> getReceipt(
                        @PathVariable Long receiptId) {

                return ResponseEntity.ok(
                                receiptService.getReceipt(receiptId));
        }

        @GetMapping("/order/{orderId}")
        public ResponseEntity<ReceiptDto.DetailResponse> getReceiptByOrderId(
                        @PathVariable Long orderId) {

                return ResponseEntity.ok(
                                receiptService.getReceiptByOrderId(orderId));
        }

        @PostMapping
        public ResponseEntity<Map<String, Object>> saveReceipt(
                        @RequestBody ReceiptDto.ReceiptCreateRequest request) {

                try {

                        receiptService.saveReceipt(request);

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "success", true,
                                                        "message", "저장 완료"));

                } catch (Exception e) {

                        e.printStackTrace();

                        return ResponseEntity.internalServerError()
                                        .body(
                                                        Map.of(
                                                                        "success", false,
                                                                        "message", e.getMessage()));
                }
        }

        @PostMapping("/test")
        public ResponseEntity<String> test(
                        @RequestBody ReceiptDto.ReceiptCreateRequest request) {

                return ResponseEntity.ok("OK");
        }
}