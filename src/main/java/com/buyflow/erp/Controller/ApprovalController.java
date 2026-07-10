package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    private static final String APPROVAL_READ_AUTHORITY =
            "hasRole('ADMIN') or hasAuthority('approvals.read') or hasAuthority('approvals.process')";

    private static final String APPROVAL_PROCESS_AUTHORITY =
            "hasRole('ADMIN') or hasAuthority('approvals.process')";

    @GetMapping
    @PreAuthorize(APPROVAL_READ_AUTHORITY)
    public ResponseEntity<PageResponse<ApprovalHistoryDto.ListResponse>> getApprovals(
            @RequestParam(name = "requestNumber", required = false, defaultValue = "") String requestNumber,
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @RequestParam(name = "requester", required = false, defaultValue = "") String requester,
            @RequestParam(name = "department", required = false, defaultValue = "") String department,
            @RequestParam(name = "status", required = false, defaultValue = "전체") String status,
            @RequestParam(name = "desiredReceiptAt", required = false, defaultValue = "") String desiredReceiptAt,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
            return ResponseEntity.ok(approvalService.getApprovals(
                    requestNumber,
                    title,
                    requester,
                    department,
                    status,
                    desiredReceiptAt,
                    page,
                    size
        ));
    }

    @GetMapping("/summary")
    @PreAuthorize(APPROVAL_READ_AUTHORITY)
    public ResponseEntity<ApprovalHistoryDto.SummaryResponse> getApprovalSummary() {
        return ResponseEntity.ok(approvalService.getApprovalSummary());
}

    @GetMapping("/{approvalId}")
    @PreAuthorize(APPROVAL_READ_AUTHORITY)
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> getApprovalDetail(
            @PathVariable(name = "approvalId") Long approvalId
    ) {
        return ResponseEntity.ok(approvalService.getApprovalDetail(approvalId));
    }

    @PostMapping("/{approvalId}/approve")
    @PreAuthorize(APPROVAL_PROCESS_AUTHORITY)
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> approve(
            @PathVariable(name = "approvalId") Long approvalId,
            @RequestBody(required = false) ApprovalHistoryDto.DecisionRequest request
    ) {
        return ResponseEntity.ok(approvalService.approve(approvalId, request));
    }

    @PostMapping("/{approvalId}/reject")
    @PreAuthorize(APPROVAL_PROCESS_AUTHORITY)
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> reject(
            @PathVariable(name = "approvalId") Long approvalId,
            @RequestBody(required = false) ApprovalHistoryDto.DecisionRequest request
    ) {
        return ResponseEntity.ok(approvalService.reject(approvalId, request));
    }

    @PatchMapping("/{approvalId}/cancel-request")
    @PreAuthorize(APPROVAL_PROCESS_AUTHORITY)
    public ResponseEntity<ApprovalHistoryDto.DetailResponse> cancelRequest(
            @PathVariable(name = "approvalId") Long approvalId
    ) {
        return ResponseEntity.ok(approvalService.cancelRequest(approvalId));
    }
}
