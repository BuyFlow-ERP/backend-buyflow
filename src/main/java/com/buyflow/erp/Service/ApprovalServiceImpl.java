package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Entity.ApprovalHistory;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.PurchaseRequest;
import com.buyflow.erp.Entity.PurchaseRequestItem;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.ApprovalHistoryRepository;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.PurchaseRequestItemRepository;
import com.buyflow.erp.Repository.PurchaseRequestRepository;
import com.buyflow.erp.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<ApprovalHistoryDto.ListResponse> getApprovals(
            String requestNumber,
            String title,
            String requester,
            String department,
            String status,
            String requestedFrom,
            String requestedTo,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        List<ApprovalHistoryDto.ListResponse> filtered = approvalHistoryRepository.findAllByOrderByApprovalIdDesc()
                .stream()
                .map(this::toListResponse)
                .filter(Objects::nonNull)
                .filter(row -> contains(row.requestNumber(), requestNumber))
                .filter(row -> contains(row.title(), title))
                .filter(row -> contains(row.requester(), requester))
                .filter(row -> isBlank(department) || Objects.equals(row.department(), department))
                .filter(row -> isBlank(status) || "전체".equals(status) || Objects.equals(row.requestStatus(), status) || Objects.equals(row.requestStatusLabel(), status))
                .filter(row -> isWithinRange(row.requestedAt(), requestedFrom, requestedTo))
                .toList();

        long totalElements = filtered.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / safeSize));
        int fromIndex = Math.min(safePage * safeSize, filtered.size());
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        return new PageResponse<>(
                filtered.subList(fromIndex, toIndex),
                new PageResponse.Pagination(safePage + 1, safeSize, totalElements, totalPages)
        );
    }

    @Override
    public ApprovalHistoryDto.DetailResponse getApprovalDetail(Long approvalId) {
        ApprovalHistory approval = findApproval(approvalId);
        PurchaseRequest request = findRequest(approval.getRequestId());

        return toDetailResponse(approval, request);
    }

    @Override
    @Transactional
    public ApprovalHistoryDto.DetailResponse approve(Long approvalId, ApprovalHistoryDto.DecisionRequest dto) {
        ApprovalHistory approval = findApproval(approvalId);
        PurchaseRequest request = findRequest(approval.getRequestId());

        approval.setApprovalStatus("APPROVED");
        approval.setCommentText(dto != null ? dto.comment() : null);
        approval.setApprovedAt(LocalDateTime.now());
        request.setRequestStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());

        approvalHistoryRepository.save(approval);
        purchaseRequestRepository.save(request);

        return toDetailResponse(approval, request);
    }

    @Override
    @Transactional
    public ApprovalHistoryDto.DetailResponse reject(Long approvalId, ApprovalHistoryDto.DecisionRequest dto) {
        ApprovalHistory approval = findApproval(approvalId);
        PurchaseRequest request = findRequest(approval.getRequestId());

        approval.setApprovalStatus("REJECTED");
        approval.setCommentText(dto != null ? dto.comment() : null);
        approval.setApprovedAt(LocalDateTime.now());
        request.setRequestStatus("REJECTED");
        request.setUpdatedAt(LocalDateTime.now());

        approvalHistoryRepository.save(approval);
        purchaseRequestRepository.save(request);

        return toDetailResponse(approval, request);
    }

    @Override
    @Transactional
    public ApprovalHistoryDto.DetailResponse cancelRequest(Long approvalId) {
        ApprovalHistory approval = findApproval(approvalId);
        PurchaseRequest request = findRequest(approval.getRequestId());

        LocalDateTime now = LocalDateTime.now();

        approval.setApprovalStatus("CANCELED");
        approval.setCommentText("요청 취소");
        approval.setApprovedAt(now);

        request.setRequestStatus("CANCELED");
        request.setUpdatedAt(now);

        approvalHistoryRepository.save(approval);
        purchaseRequestRepository.save(request);

        return toDetailResponse(approval, request);
}

    private ApprovalHistoryDto.ListResponse toListResponse(ApprovalHistory approval) {
        PurchaseRequest request = purchaseRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (request == null || "Y".equalsIgnoreCase(nullToEmpty(request.getDeletedYn()).trim())) {
            return null;
        }

        return new ApprovalHistoryDto.ListResponse(
        approval.getApprovalId(),
        request.getRequestId(),
        nullToEmpty(request.getRequestNo()),
        nullToEmpty(request.getTitle()),
        getUserName(request.getRequestorId()),
        "-",
        formatDate(request.getCreatedAt()),
        formatDate(request.getDueDate()),
        formatDateTime(request.getCreatedAt()),
        formatDateTime(request.getUpdatedAt()),
        request.getTotalAmount() != null ? request.getTotalAmount() : calculateTotalAmount(request.getRequestId()),
        resolvePriorityLabel(request),
        toStatusCode(request.getRequestStatus()),
        toRequestStatusLabel(request.getRequestStatus()),
        createStepLabel(approval),
        getApproverName(approval.getApproverId())
);
    }

private ApprovalHistoryDto.DetailResponse toDetailResponse(ApprovalHistory approval, PurchaseRequest request) {
    Users requester = request.getRequestorId() == null
            ? null
            : userRepository.findById(request.getRequestorId()).orElse(null);

    Users approver = approval.getApproverId() == null
            ? null
            : userRepository.findById(approval.getApproverId()).orElse(null);

    return new ApprovalHistoryDto.DetailResponse(
            approval.getApprovalId(),
            request.getRequestId(),
            nullToEmpty(request.getRequestNo()),
            nullToEmpty(request.getTitle()),
            new ApprovalHistoryDto.UserInfo(
                    requester != null ? requester.getUserId() : request.getRequestorId(),
                    requester != null ? nullToEmpty(requester.getUserName()) : getUserName(request.getRequestorId()),
                    ""
            ),
            new ApprovalHistoryDto.DepartmentInfo(null, "-"),
            formatDate(request.getCreatedAt()),
            formatDate(request.getDueDate()),
            formatDateTime(request.getCreatedAt()),
            formatDateTime(request.getUpdatedAt()),
            resolvePriorityLabel(request),
            toStatusCode(request.getRequestStatus()),
            toRequestStatusLabel(request.getRequestStatus()),
            nullToEmpty(request.getReason()),
            getApprovalItemResponses(request.getRequestId()),
            List.of(),
            new ApprovalHistoryDto.CurrentStep(
                    createStepLabel(approval),
                    new ApprovalHistoryDto.UserInfo(
                            approver != null ? approver.getUserId() : approval.getApproverId(),
                            approver != null ? nullToEmpty(approver.getUserName()) : getApproverName(approval.getApproverId()),
                            ""
                    )
            ),
            getHistoryResponses(request.getRequestId())
    );
}

    private List<ApprovalHistoryDto.ApprovalItemResponse> getApprovalItemResponses(Long requestId) {
        List<PurchaseRequestItem> items = purchaseRequestItemRepository.findByRequestIdOrderByRequestItemIdAsc(requestId);
        Map<Long, Product> productMap = productRepository.findAllById(
                        items.stream()
                                .map(PurchaseRequestItem::getProductId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return items.stream()
                .map(item -> {
                    Product product = productMap.get(item.getProductId());
                    int quantity = item.getRequestQuantity() != null ? item.getRequestQuantity() : 0;
                    int unitPrice = item.getEstimatedUnitPrice() != null ? item.getEstimatedUnitPrice() : 0;


return new ApprovalHistoryDto.ApprovalItemResponse(
        item.getRequestItemId(),
        item.getProductId(),
        product != null ? nullToEmpty(product.getProductNo()) : "",
        product != null ? nullToEmpty(product.getProductName()) : "",
        product != null ? nullToEmpty(product.getCategoryName()) : "",
        product != null ? nullToEmpty(product.getSpec()) : "",
        quantity,
        product != null ? nullToEmpty(product.getUnit()) : "",
        unitPrice,
        quantity * unitPrice,
        nullToEmpty(item.getRemark()),
        formatDateTime(item.getCreatedAt()),
        formatDateTime(item.getUpdatedAt())
);
                })
                .toList();
    }

    private List<ApprovalHistoryDto.HistoryResponse> getHistoryResponses(Long requestId) {
        return approvalHistoryRepository.findByRequestIdOrderByApprovalStepAsc(requestId)
                .stream()
                .map(history -> new ApprovalHistoryDto.HistoryResponse(
                        history.getApprovalId(),
                        toHistoryStatus(history.getApprovalStatus()),
                        createHistoryTitle(history.getApprovalStatus()),
                        getApproverName(history.getApproverId()),
                        "",
                        formatDateTime(history.getApprovedAt()),
                        isBlank(history.getCommentText()) ? toRequestStatusLabel(history.getApprovalStatus()) : history.getCommentText()
                ))
                .toList();
    }

    private ApprovalHistory findApproval(Long approvalId) {
        return approvalHistoryRepository.findById(approvalId)
                .orElseThrow(() -> new EntityNotFoundException("승인 이력을 찾을 수 없습니다. ID: " + approvalId));
    }

    private PurchaseRequest findRequest(Long requestId) {
        return purchaseRequestRepository.findById(requestId)
                .filter(request -> !"Y".equalsIgnoreCase(nullToEmpty(request.getDeletedYn()).trim()))
                .orElseThrow(() -> new EntityNotFoundException("구매 요청을 찾을 수 없습니다. ID: " + requestId));
    }

    private int calculateTotalAmount(Long requestId) {
        return purchaseRequestItemRepository.findByRequestIdOrderByRequestItemIdAsc(requestId)
                .stream()
                .mapToInt(item -> (item.getRequestQuantity() != null ? item.getRequestQuantity() : 0)
                        * (item.getEstimatedUnitPrice() != null ? item.getEstimatedUnitPrice() : 0))
                .sum();
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return "-";
        }
        return userRepository.findById(userId)
                .map(Users::getUserName)
                .filter(name -> !isBlank(name))
                .orElse("사용자 " + userId);
    }

    private String getApproverName(Long approverId) {
        if (approverId == null) {
            return "-";
        }
        return userRepository.findById(approverId)
                .map(Users::getUserName)
                .filter(name -> !isBlank(name))
                .orElse("사용자 " + approverId);
    }

    private String createStepLabel(ApprovalHistory approval) {
        if (approval.getApprovalStep() == null) {
            return "승인 단계";
        }
        return approval.getApprovalStep() + "차 승인";
    }

    private String createHistoryTitle(String status) {
        return switch (toStatusCode(status)) {
            case "APPROVED" -> "승인 완료";
            case "REJECTED" -> "승인 반려";
            case "CANCEL_REQUESTED" -> "요청 취소";
            default -> "승인 검토 중";
        };
    }

    private String toHistoryStatus(String status) {
        String code = toStatusCode(status);
        if ("APPROVED".equals(code) || "REJECTED".equals(code)) {
            return "DONE";
        }
        return "CURRENT";
    }

    private String toStatusCode(String status) {
        if (status == null) {
            return "PENDING_APPROVAL";
        }
        return switch (status.trim().toUpperCase()) {
            case "DRAFT" -> "DRAFT";
            case "PENDING", "PENDING_APPROVAL", "WAITING", "REQUESTED" -> "PENDING_APPROVAL";
            case "APPROVED" -> "APPROVED";
            case "REJECTED" -> "REJECTED";
            case "ORDERED" -> "ORDERED";
            case "CANCEL_REQUESTED", "CANCELED", "CANCELLED" -> "CANCEL_REQUESTED";
            default -> status;
        };
    }

    private String toRequestStatusLabel(String status) {
        return switch (toStatusCode(status)) {
            case "DRAFT" -> "임시 저장";
            case "PENDING_APPROVAL" -> "승인 대기";
            case "APPROVED" -> "승인 완료";
            case "REJECTED" -> "반려";
            case "ORDERED" -> "발주 완료";
            case "CANCEL_REQUESTED" -> "요청 취소";
            default -> status == null ? "승인 대기" : status;
        };
    }

    private String resolvePriorityLabel(PurchaseRequest request) {
        if (request.getDueDate() != null && !request.getDueDate().isAfter(LocalDate.now().plusDays(3))) {
            return "긴급";
        }
        return "일반";
    }

    private boolean contains(String value, String keyword) {
        return isBlank(keyword) || nullToEmpty(value).toLowerCase().contains(keyword.trim().toLowerCase());
    }

    private boolean isWithinRange(String value, String from, String to) {
        if (isBlank(value)) {
            return true;
        }
        if (!isBlank(from) && value.compareTo(from) < 0) {
            return false;
        }
        return isBlank(to) || value.compareTo(to) <= 0;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? "" : value.toLocalDate().format(DATE_FORMATTER);
    }

    private String formatDate(LocalDate value) {
        return value == null ? "" : value.format(DATE_FORMATTER);
    }

    private String formatDateTime(LocalDateTime value) {
    return value == null ? "" : value.format(DATE_TIME_FORMATTER);
}
}
