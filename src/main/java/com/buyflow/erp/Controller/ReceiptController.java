package com.buyflow.erp.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Service.ReceiptService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Service.ExcelService;

@RestController
@RequiredArgsConstructor
@RequestMapping({ "/receipts", "/api/receipts" })
public class ReceiptController {

        private final ReceiptService receiptService;
        private final ExcelService excelService;

        @GetMapping("/test")
        public String test() {
                return "receipt ok";
        }

        @GetMapping
        public ResponseEntity<ReceiptDto.PageResponse<ReceiptDto.ListResponse>> getReceipts(
                        @RequestParam(name = "activeTab", required = false) String activeTab,
                        @RequestParam(name = "orderNumber", required = false) String orderNumber,
                        @RequestParam(name = "supplierKeyword", required = false) String supplierKeyword,
                        @RequestParam(name = "itemKeyword", required = false) String itemKeyword,
                        @RequestParam(name = "warehouseName", required = false) String warehouseName,
                        @RequestParam(name = "expectedFrom", required = false) String expectedFrom,
                        @RequestParam(name = "expectedTo", required = false) String expectedTo,
                        @RequestParam(name = "status", required = false) String status,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {

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
                        @PathVariable(name = "receiptId") Long receiptId) {

                return ResponseEntity.ok(
                                receiptService.getReceipt(receiptId));
        }

        @GetMapping("/order/{orderId}")
        public ResponseEntity<ReceiptDto.DetailResponse> getReceiptByOrderId(
                        @PathVariable(name = "orderId") Long orderId) {

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

        @GetMapping("/excel")
        public void exportExcel(HttpServletResponse response) throws IOException {

                Users testUser = new Users();
                testUser.setUserId(5L);

                excelService.exportExcel(
                                "receipts",
                                testUser,
                                response);
        }
}