package com.buyflow.erp.Controller;

import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Service.PurchaseOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase-order-items")
public class PurchaseOrderItemController {

    private final PurchaseOrderItemService purchaseOrderItemService;

    @GetMapping
    public List<PurchaseOrderItem> getOrderItems() {

        return purchaseOrderItemService.getOrderItems();
    }
    @PostMapping
public ResponseEntity<String> saveOrderItem(
        @RequestBody PurchaseOrderItemDto.CreateRequest request) {

    purchaseOrderItemService.saveOrderItem(request);

    return ResponseEntity.ok("저장 완료");
}
}