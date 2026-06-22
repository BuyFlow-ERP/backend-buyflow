package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/receipts", "/api/receipts"})
public class ReceiptController {

    private final ReceiptService receiptService;

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
            @RequestParam(defaultValue = "10") int size
    ) {
        ReceiptDto.PageResponse<ReceiptDto.ListResponse> result =
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
                        size
                );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter-options")
    public ResponseEntity<ReceiptDto.FilterOptionsResponse> getFilterOptions() {
        return ResponseEntity.ok(receiptService.getFilterOptions());
    }

    @GetMapping("/summary")
    public ResponseEntity<ReceiptDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(receiptService.getSummary());
    }

    @GetMapping("/{receiptId:\\d+}")
    public ResponseEntity<ReceiptDto.DetailResponse> getReceipt(
            @PathVariable Long receiptId
    ) {
        return ResponseEntity.ok(
                receiptService.getReceipt(receiptId)
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveReceipt(
            @RequestBody ReceiptDto.ReceiptCreateRequest request
    ) {
        try {
            System.out.println("POST /receipts 호출");

            receiptService.saveReceipt(request);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "저장 완료"
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(
            @RequestBody ReceiptDto.ReceiptCreateRequest request
    ) {
        return ResponseEntity.ok(request.getReceiptNo());
    }
}