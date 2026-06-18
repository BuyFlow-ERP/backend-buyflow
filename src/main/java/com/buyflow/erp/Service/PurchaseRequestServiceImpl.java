package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Override
    public PageResponse<PurchaseRequestDto.ListResponse> getPurchaseRequests(
            String requestNumber,
            String title,
            String requester,
            String department,
            String status,
            String priority,
            String requestedFrom,
            String requestedTo,
            String desiredInboundFrom,
            String desiredInboundTo,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        List<PurchaseRequestDto.ListResponse> filtered = purchaseRequestRepository.findActiveRequestsOrderByRequestIdDesc()
                .stream()
                .map(this::toListResponse)
                .filter(row -> contains(row.requestNumber(), requestNumber))
                .filter(row -> contains(row.title(), title))
                .filter(row -> contains(row.requester(), requester))
                .filter(row -> isAllOrEquals(department, "전체 부서", row.department()))
                .filter(row -> isAllOrEquals(status, "전체", row.status()))
                .filter(row -> isAllOrEquals(priority, "전체", row.priority()))
                .filter(row -> isWithinRange(row.requestedAt(), requestedFrom, requestedTo))
                .filter(row -> isWithinRange(row.desiredInboundAt(), desiredInboundFrom, desiredInboundTo))
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
    public PurchaseRequestDto.DetailResponse getPurchaseRequestDetail(Long requestId) {
        PurchaseRequest request = purchaseRequestRepository.findById(requestId)
                .filter(this::isActive)
                .orElseThrow(() -> new EntityNotFoundException("구매 요청을 찾을 수 없습니다. ID: " + requestId));

        List<PurchaseRequestDto.ItemResponse> items = getItemResponses(requestId);
        int calculatedTotalAmount = items.stream()
                .mapToInt(PurchaseRequestDto.ItemResponse::estimatedAmount)
                .sum();

            return new PurchaseRequestDto.DetailResponse(
                request.getRequestId(),
                nullToEmpty(request.getRequestNo()),
                nullToEmpty(request.getTitle()),
                getUserName(request.getRequestorId()),
                "-",
                formatDate(request.getCreatedAt()),
                formatDate(request.getDueDate()),
                formatDateTime(request.getCreatedAt()),
                formatDateTime(request.getUpdatedAt()),
                resolvePriorityLabel(request),
                toRequestStatusLabel(request.getRequestStatus()),
                nullToEmpty(request.getReason()),
                request.getTotalAmount() != null ? request.getTotalAmount() : calculatedTotalAmount,
                items,
                List.of()
                );
         }

    @Override
    public PurchaseRequestDto.SummaryResponse getPurchaseRequestSummary() {
        List<PurchaseRequest> requests = purchaseRequestRepository.findActiveRequestsOrderByRequestIdDesc();

        return new PurchaseRequestDto.SummaryResponse(
                requests.size(),
                countByStatusLabel(requests, "임시 저장"),
                countByStatusLabel(requests, "승인 대기"),
                countByStatusLabel(requests, "승인 완료"),
                countByStatusLabel(requests, "반려"),
                countByStatusLabel(requests, "발주 완료")
        );
    }

    @Override
    public Map<String, Object> getFilterOptions() {
        Set<String> departments = new LinkedHashSet<>();
        departments.add("전체 부서");
        departments.add("-");

        Set<String> statuses = new LinkedHashSet<>(List.of("전체", "승인 대기", "승인 완료", "반려", "발주 완료"));
        Set<String> priorities = new LinkedHashSet<>(List.of("전체", "일반", "긴급"));

        return Map.of(
                "departments", List.copyOf(departments),
                "statuses", List.copyOf(statuses),
                "priorities", List.copyOf(priorities)
        );
    }

    @Override
    @Transactional
    public PurchaseRequestDto.DetailResponse createPurchaseRequest(PurchaseRequestDto.CreateRequest dto) {
        long requestId = nextRequestId();
        LocalDateTime now = LocalDateTime.now();

        PurchaseRequest request = new PurchaseRequest();
        request.setRequestId(requestId);
        request.setRequestNo(isBlank(dto.requestNumber()) ? createRequestNumber(requestId) : dto.requestNumber());
        Long requestorId = dto.requestorId();

    if (requestorId == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
            "요청자 ID는 필수입니다."
    );
}

        if (!userRepository.existsById(requestorId)) {
            throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "존재하지 않는 요청자 ID입니다: " + requestorId
            );
        }

        request.setRequestorId(requestorId);
        request.setTitle(nullToEmpty(dto.title()));
        request.setReason(nullToEmpty(dto.reason()));
        request.setDueDate(parseDate(dto.expectedDate()));
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        request.setRequestStatus(normalizeRequestStatus(dto.status()));
        request.setDeletedYn("N");

        List<PurchaseRequestItem> items = new ArrayList<>();
        long nextItemId = nextRequestItemId();
        int totalAmount = 0;

        for (PurchaseRequestDto.CreateItemRequest itemDto : dto.items() == null ? List.<PurchaseRequestDto.CreateItemRequest>of() : dto.items()) {
            int quantity = itemDto.requestQuantity() != null ? itemDto.requestQuantity() : 0;
            int unitPrice = itemDto.estimatedUnitPrice() != null ? itemDto.estimatedUnitPrice() : 0;
            totalAmount += quantity * unitPrice;

            PurchaseRequestItem item = new PurchaseRequestItem();
            item.setRequestItemId(nextItemId++);
            item.setRequestId(requestId);
            item.setProductId(itemDto.productId());
            item.setRequestQuantity(quantity);
            item.setEstimatedUnitPrice(unitPrice);
            item.setRemark(itemDto.remark());
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            items.add(item);
        }

    request.setTotalAmount(totalAmount);
    purchaseRequestRepository.save(request);
    purchaseRequestItemRepository.saveAll(items);

    if ("PENDING_APPROVAL".equals(request.getRequestStatus())) {
    ApprovalHistory approvalHistory = new ApprovalHistory();

    approvalHistory.setApprovalId(nextApprovalId());
    approvalHistory.setRequestId(requestId);
    approvalHistory.setApproverId(101L);
    approvalHistory.setApprovalStatus("PENDING_APPROVAL");
    approvalHistory.setCommentText("구매 요청 승인 대기");
    approvalHistory.setApprovedAt(null);
    approvalHistory.setApprovalStep(1);

    approvalHistoryRepository.save(approvalHistory);
}

            return getPurchaseRequestDetail(requestId);
    }

    private PurchaseRequestDto.ListResponse toListResponse(PurchaseRequest request) {
        return new PurchaseRequestDto.ListResponse(
                request.getRequestId(),
                nullToEmpty(request.getRequestNo()),
                nullToEmpty(request.getTitle()),
                getUserName(request.getRequestorId()),
                "-",
                formatDate(request.getCreatedAt()),
                formatDate(request.getDueDate()),
                formatDateTime(request.getCreatedAt()),
                formatDateTime(request.getUpdatedAt()),
                purchaseRequestItemRepository.countByRequestId(request.getRequestId()),
                request.getTotalAmount() != null ? request.getTotalAmount() : 0,
                resolvePriorityLabel(request),
                toRequestStatusLabel(request.getRequestStatus())
        );
    }

    private List<PurchaseRequestDto.ItemResponse> getItemResponses(Long requestId) {
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
                .map(item -> toItemResponse(item, productMap.get(item.getProductId())))
                .toList();
    }

    private PurchaseRequestDto.ItemResponse toItemResponse(PurchaseRequestItem item, Product product) {
        int quantity = item.getRequestQuantity() != null ? item.getRequestQuantity() : 0;
        int unitPrice = item.getEstimatedUnitPrice() != null ? item.getEstimatedUnitPrice() : 0;


    return new PurchaseRequestDto.ItemResponse(
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

    }

    private long countByStatusLabel(List<PurchaseRequest> requests, String label) {
        return requests.stream()
                .filter(request -> label.equals(toRequestStatusLabel(request.getRequestStatus())))
                .count();
    }

    private boolean isActive(PurchaseRequest request) {
        return !"Y".equalsIgnoreCase(nullToEmpty(request.getDeletedYn()).trim());
    }

    private boolean contains(String value, String keyword) {
        return isBlank(keyword) || nullToEmpty(value).toLowerCase().contains(keyword.trim().toLowerCase());
    }

    private boolean isAllOrEquals(String filter, String allValue, String value) {
        return isBlank(filter) || allValue.equals(filter) || Objects.equals(filter, value);
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

    private String getUserName(Long userId) {
        if (userId == null) {
            return "-";
        }
        return userRepository.findById(userId)
                .map(Users::getUserName)
                .filter(name -> !isBlank(name))
                .orElse("사용자 " + userId);
    }

    private String resolvePriorityLabel(PurchaseRequest request) {
        if (request.getDueDate() != null && !request.getDueDate().isAfter(LocalDate.now().plusDays(3))) {
            return "긴급";
        }
        return "일반";
    }

    private String toRequestStatusLabel(String status) {
        if (status == null) {
            return "승인 대기";
        }
        return switch (status.trim().toUpperCase()) {
            case "DRAFT" -> "임시 저장";
            case "PENDING", "PENDING_APPROVAL", "WAITING", "REQUESTED" -> "승인 대기";
            case "APPROVED" -> "승인 완료";
            case "REJECTED" -> "반려";
            case "ORDERED" -> "발주 완료";
            case "CANCEL_REQUESTED", "CANCELED", "CANCELLED" -> "요청 취소";
            default -> status;
        };
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

    private String formatDateTime(LocalDateTime value) {
    return value == null ? "" : value.format(DATE_TIME_FORMATTER);
}

    private String formatDate(LocalDate value) {
        return value == null ? "" : value.format(DATE_FORMATTER);
    }

    private LocalDate parseDate(String value) {
        if (isBlank(value)) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private long nextRequestId() {
        return purchaseRequestRepository.findAll()
                .stream()
                .map(PurchaseRequest::getRequestId)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    private long nextRequestItemId() {
        return purchaseRequestItemRepository.findAll()
                .stream()
                .map(PurchaseRequestItem::getRequestItemId)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    private long nextApprovalId() {
        return approvalHistoryRepository.findAll()
                .stream()
                .map(ApprovalHistory::getApprovalId)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(920000L) + 1;
}

    private String createRequestNumber(long requestId) {
        return "PR-" + LocalDate.now().getYear() + "-" + String.format("%04d", requestId);
    }

    private String normalizeRequestStatus(String status) {
    if (isBlank(status)) {
        return "PENDING_APPROVAL";
    }

    return switch (status.trim().toUpperCase()) {
        case "DRAFT" -> "DRAFT";
        case "PENDING", "PENDING_APPROVAL", "WAITING", "REQUESTED" -> "PENDING_APPROVAL";
        case "APPROVED" -> "APPROVED";
        case "REJECTED" -> "REJECTED";
        case "ORDERED" -> "ORDERED";
        case "CANCELED", "CANCELLED", "CANCEL_REQUESTED" -> "CANCELED";
        default -> "PENDING_APPROVAL";
    };
}
}

