package com.buyflow.erp.Controller;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    // @GetMapping("/{orderId}")
    // public ResponseEntity<PurchaseOrder> get(@PathVariable Long orderId) {
    //     return ResponseEntity.ok(service.getOrderWithItems(orderId));
    // }

    // @PostMapping
    // public ResponseEntity<PurchaseOrder> create(@RequestBody PurchaseOrderCreateRequest request) {
    //     return ResponseEntity.ok(service.createOrder(request));
    // }

    // @PutMapping("/{orderId}")
    // public ResponseEntity<PurchaseOrder> update(@PathVariable Long orderId, @RequestBody PurchaseOrderUpdateRequest request) {
    //     return ResponseEntity.ok(service.updateOrder(orderId, request));
    // }

    // @DeleteMapping("/{orderId}")
    // public ResponseEntity<Void> delete(@PathVariable Long orderId) {
    //     service.deleteOrder(orderId);
    //     return ResponseEntity.noContent().build();
    // }
    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderResponse> getOrder(@PathVariable Long orderId) {   // 메서드명 변경 + Response
        PurchaseOrderResponse response = service.getOrderWithItems(orderId);   // DTO 반환
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(@RequestBody PurchaseOrderRequest request) {  // Request 통합
        PurchaseOrder savedOrder = service.createOrder(request);
        PurchaseOrderResponse response = PurchaseOrderResponse.from(savedOrder);   // Entity → DTO 변환
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderResponse> updateOrder(   // 메서드명 변경 + Response
            @PathVariable Long orderId,
            @RequestBody PurchaseOrderRequest request) {        // UpdateRequest → Request

        PurchaseOrder updatedOrder = service.updateOrder(orderId, request);
        PurchaseOrderResponse response = PurchaseOrderResponse.from(updatedOrder);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {   // 메서드명 변경
        service.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
