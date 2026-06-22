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
            @RequestParam(name = "activeTab", required = false) String activeTab,
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "supplierKeyword", required = false) String supplierKeyword,
            @RequestParam(name = "itemKeyword", required = false) String itemKeyword,
            @RequestParam(name = "warehouseName", required = false) String warehouseName,
            @RequestParam(name = "expectedFrom", required = false) String expectedFrom,
            @RequestParam(name = "expectedTo", required = false) String expectedTo,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
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
            @PathVariable(name = "receiptId") Long receiptId
    ) {
        return ResponseEntity.ok(
                receiptService.getReceipt(receiptId)
        );
    }

@GetMapping("/order/{orderId}")
public ResponseEntity<ReceiptDto.DetailResponse> getReceiptByOrder(
        @PathVariable Long orderId
) {

    System.out.println("ORDER API 호출: " + orderId);

    return ResponseEntity.ok(
            receiptService.getReceiptByOrderId(orderId)
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