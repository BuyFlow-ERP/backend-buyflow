package com.buyflow.erp.Controller;

public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrder> get(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getOrderWithItems(orderId));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(@RequestBody PurchaseOrderCreateRequest request) {
        return ResponseEntity.ok(service.createOrder(request));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<PurchaseOrder> update(@PathVariable Long orderId, @RequestBody PurchaseOrderUpdateRequest request) {
        return ResponseEntity.ok(service.updateOrder(orderId, request));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId) {
        service.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
