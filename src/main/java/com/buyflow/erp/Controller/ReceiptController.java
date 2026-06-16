package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Service.ReceiptService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public List<Receipt> getReceipts() {
        return receiptService.getReceipts();
    }

    @GetMapping("/{receiptId}")
    public Receipt getReceipt(
            @PathVariable Long receiptId) {

        return receiptService.getReceipt(receiptId);
            }
@PostMapping
public ResponseEntity<String> saveReceipt(
        @RequestBody ReceiptDto.ReceiptCreateRequest request) {

    try {

        System.out.println("POST /receipts 호출");

        receiptService.saveReceipt(request);

        return ResponseEntity.ok("저장 완료");

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity.internalServerError()
                .body(e.toString());
    }
}
@PostMapping("/test")
public ResponseEntity<String> test(
       @RequestBody ReceiptDto.ReceiptCreateRequest request) {

    return ResponseEntity.ok(
            request.getReceiptNo()
    );
}
    }
