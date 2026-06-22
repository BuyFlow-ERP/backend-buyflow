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
            receiptService.saveReceipt(request);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "저장 완료"
                    )
            );

        } catch (Exception e) {

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