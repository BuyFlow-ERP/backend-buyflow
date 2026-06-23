package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @GetMapping
    public ResponseEntity<PageResponse<PurchaseRequestDto.ListResponse>> getPurchaseRequests(
            @RequestParam(name = "requestNumber", required = false, defaultValue = "") String requestNumber,
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @RequestParam(name = "requester", required = false, defaultValue = "") String requester,
            @RequestParam(name = "department", required = false, defaultValue = "전체 부서") String department,
            @RequestParam(name = "status", required = false, defaultValue = "전체") String status,
            @RequestParam(name = "priority", required = false, defaultValue = "전체") String priority,
            @RequestParam(name = "requestedFrom", required = false, defaultValue = "") String requestedFrom,
            @RequestParam(name = "requestedTo", required = false, defaultValue = "") String requestedTo,
            @RequestParam(name = "desiredReceiptFrom", required = false, defaultValue = "") String desiredReceiptFrom,
            @RequestParam(name = "desiredReceiptTo", required = false, defaultValue = "") String desiredReceiptTo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(purchaseRequestService.getPurchaseRequests(
                requestNumber,
                title,
                requester,
                department,
                status,
                priority,
                requestedFrom,
                requestedTo,
                desiredReceiptFrom,
                desiredReceiptTo,
                page,
                size
        ));
    }

    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        return ResponseEntity.ok(purchaseRequestService.getFilterOptions());
    }

    @GetMapping("/summary")
    public ResponseEntity<PurchaseRequestDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(purchaseRequestService.getPurchaseRequestSummary());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<PurchaseRequestDto.DetailResponse> getPurchaseRequestDetail(
            @PathVariable(name = "requestId") Long requestId
    ) {
        return ResponseEntity.ok(purchaseRequestService.getPurchaseRequestDetail(requestId));
    }

    @PostMapping
    public ResponseEntity<PurchaseRequestDto.DetailResponse> createPurchaseRequest(
            @RequestBody PurchaseRequestDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseRequestService.createPurchaseRequest(request));
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<PurchaseRequestDto.DetailResponse> updatePurchaseRequest(
            @PathVariable(name = "requestId") Long requestId,
            @RequestBody PurchaseRequestDto.UpdateRequest request
    ) {
        return ResponseEntity.ok(
            purchaseRequestService.updatePurchaseRequest(requestId, request)
            );
        }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<PurchaseRequestDto.DetailResponse> cancelPurchaseRequest(
        @PathVariable(name = "requestId") Long requestId
    ) {
        return ResponseEntity.ok(
            purchaseRequestService.cancelPurchaseRequest(requestId)
            );
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deletePurchaseRequest(
        @PathVariable(name = "requestId") Long requestId
    ) {
        purchaseRequestService.deletePurchaseRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
