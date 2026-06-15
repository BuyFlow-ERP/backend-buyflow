package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ReceiptItemDto;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Service.ReceiptItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipt-items")
public class ReceiptItemController {

    private final ReceiptItemService receiptItemService;

    @GetMapping
    public List<ReceiptItem> getReceiptItems() {

        return receiptItemService.getReceiptItems();
    }

    @PostMapping
    public ResponseEntity<String> saveReceiptItem(
            @RequestBody ReceiptItemDto.CreateRequest request) {

        receiptItemService.saveReceiptItem(request);

        return ResponseEntity.ok("저장 완료");
    }

    @PutMapping("/{receiptItemId}")
    public ResponseEntity<String> updateReceiptItem(
            @PathVariable Long receiptItemId,
            @RequestBody ReceiptItemDto.CreateRequest request) {

        receiptItemService.updateReceiptItem(
                receiptItemId,
                request
        );

        return ResponseEntity.ok("수정 완료");
    }
}