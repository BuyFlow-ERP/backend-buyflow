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
            @RequestParam(required = false, defaultValue = "") String requestNumber,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String requester,
            @RequestParam(required = false, defaultValue = "전체 부서") String department,
            @RequestParam(required = false, defaultValue = "전체") String status,
            @RequestParam(required = false, defaultValue = "전체") String priority,
            @RequestParam(required = false, defaultValue = "") String requestedFrom,
            @RequestParam(required = false, defaultValue = "") String requestedTo,
            @RequestParam(required = false, defaultValue = "") String desiredInboundFrom,
            @RequestParam(required = false, defaultValue = "") String desiredInboundTo,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "15") int size
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
                desiredInboundFrom,
                desiredInboundTo,
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
            @PathVariable Long requestId
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
}
