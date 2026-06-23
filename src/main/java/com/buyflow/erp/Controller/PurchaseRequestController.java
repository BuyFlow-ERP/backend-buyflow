package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.buyflow.erp.Entity.Attachment;
import com.buyflow.erp.Repository.AttachmentRepository;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;
    private final AttachmentRepository attachmentRepository;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<PurchaseRequestDto.DetailResponse> createPurchaseRequest(
        @RequestPart("data") PurchaseRequestDto.CreateRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(purchaseRequestService.createPurchaseRequest(request, file));
}

    @PutMapping(
        value = "/{requestId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
        public ResponseEntity<PurchaseRequestDto.DetailResponse> updatePurchaseRequest(
            @PathVariable(name = "requestId") Long requestId,
            @RequestPart("data") PurchaseRequestDto.UpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(
            purchaseRequestService.updatePurchaseRequest(requestId, request, file)
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

    @GetMapping("/attachments/{attachmentId}/download")
        public ResponseEntity<Resource> downloadAttachment(
            @PathVariable(name = "attachmentId") Long attachmentId
    ) throws Exception {
        Attachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "첨부파일을 찾을 수 없습니다. attachmentId=" + attachmentId
            ));

    Path path = Path.of(attachment.getFilePath());

        if (!Files.exists(path)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "첨부파일 실제 파일을 찾을 수 없습니다."
        );
    }

    Resource resource = new PathResource(path);

    String encodedFileName = URLEncoder.encode(
            attachment.getOriginalName(),
            StandardCharsets.UTF_8
    ).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                            .filename(encodedFileName, StandardCharsets.UTF_8)
                            .build()
                            .toString()
            )
            .body(resource);
  }
}
