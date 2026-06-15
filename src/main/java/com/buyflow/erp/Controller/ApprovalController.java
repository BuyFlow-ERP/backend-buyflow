package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<PageResponse<ApprovalHistoryDto.ListResponse>> getApprovals(
            @RequestParam(required = false, defaultValue = "") String requestNumber,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String requester,
            @RequestParam(required = false, defaultValue = "") String department,
            @RequestParam(required = false, defaultValue = "전체") String status,
            @RequestParam(required = false, defaultValue = "") String requestedFrom,
            @RequestParam(required = false, defaultValue = "") String requestedTo,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(approvalService.getApprovals(
                requestNumber,
                title,
                requester,
                department,
                status,
                requestedFrom,
                requestedTo,
                page,
                size
        ));
    }

    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> getApprovalDetail(
            @PathVariable Long approvalId
    ) {
        return ResponseEntity.ok(approvalService.getApprovalDetail(approvalId));
    }

    @PostMapping("/{approvalId}/approve")
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> approve(
            @PathVariable Long approvalId,
            @RequestBody(required = false) ApprovalHistoryDto.DecisionRequest request
    ) {
        return ResponseEntity.ok(approvalService.approve(approvalId, request));
    }

    @PostMapping("/{approvalId}/reject")
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> reject(
            @PathVariable Long approvalId,
            @RequestBody(required = false) ApprovalHistoryDto.DecisionRequest request
    ) {
        return ResponseEntity.ok(approvalService.reject(approvalId, request));
    }

    @PatchMapping("/{approvalId}/cancel-request")
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> cancelRequest(
            @PathVariable Long approvalId
    ) {
        return ResponseEntity.ok(approvalService.cancelRequest(approvalId));
    }
}
