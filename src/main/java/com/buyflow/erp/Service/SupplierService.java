package com.buyflow.erp.Service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.SupplierFilterOptionsResponse;
import com.buyflow.erp.Dto.SupplierPageResponse;
import com.buyflow.erp.Dto.SupplierRequest;
import com.buyflow.erp.Dto.SupplierResponse;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private static final int MAX_PAGE_SIZE = 100;

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public SupplierPageResponse search(
            String supplierCode,
            String supplierName,
            String manager,
            String tradeStatus,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Supplier> suppliers = supplierRepository.search(
                blankToNull(supplierCode),
                blankToNull(supplierName),
                blankToNull(manager),
                normalizeTradeStatusForSearch(tradeStatus),
                pageable
        );

        return new SupplierPageResponse(
                suppliers.stream().map(SupplierResponse::from).toList(),
                new SupplierPageResponse.Pagination(
                        suppliers.getNumber() + 1,
                        suppliers.getSize(),
                        suppliers.getTotalElements(),
                        Math.max(suppliers.getTotalPages(), 1)
                )
        );
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(Long supplierId) {
        return SupplierResponse.from(findActiveSupplier(supplierId));
    }

    @Transactional(readOnly = true)
    public SupplierFilterOptionsResponse findFilterOptions() {
        return SupplierFilterOptionsResponse.defaults();
    }

    @Transactional(readOnly = true)
    public boolean existsBusinessNumber(String businessNumber, Long excludeSupplierId) {
        String normalizedBusinessNumber = normalizeBusinessNumber(businessNumber);

        if (normalizedBusinessNumber == null) {
            return false;
        }

        return excludeSupplierId == null
                ? supplierRepository.existsByBusinessNumber(normalizedBusinessNumber)
                : supplierRepository.existsByBusinessNumberAndSupplierIdNot(normalizedBusinessNumber, excludeSupplierId);
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        validateSupplierCode(request.supplierCode(), null);
        validateBusinessNumber(request.businessNumber(), null);

        LocalDateTime now = LocalDateTime.now();
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(blankToNull(request.supplierCode()));
        applyRequest(supplier, request);
        supplier.setUseYn("Y");
        supplier.setCreatedAt(now);
        supplier.setUpdatedAt(now);

        Supplier savedSupplier = supplierRepository.save(supplier);

        if (!StringUtils.hasText(savedSupplier.getSupplierCode())) {
            savedSupplier.setSupplierCode(createDefaultSupplierCode(savedSupplier.getSupplierId()));
        }

        return SupplierResponse.from(savedSupplier);
    }

    @Transactional
    public SupplierResponse update(Long supplierId, SupplierRequest request) {
        Supplier supplier = findActiveSupplier(supplierId);
        validateSupplierCode(request.supplierCode(), supplierId);
        validateBusinessNumber(request.businessNumber(), supplierId);

        supplier.setSupplierCode(blankToNull(request.supplierCode()));
        applyRequest(supplier, request);
        supplier.setUpdatedAt(LocalDateTime.now());

        return SupplierResponse.from(supplier);
    }

    @Transactional
    public SupplierResponse changeTradeStatus(Long supplierId, String tradeStatus) {
        Supplier supplier = findActiveSupplier(supplierId);
        supplier.setTradeStatus(normalizeTradeStatus(tradeStatus));
        supplier.setUpdatedAt(LocalDateTime.now());

        return SupplierResponse.from(supplier);
    }

    @Transactional
    public void delete(Long supplierId) {
        Supplier supplier = findActiveSupplier(supplierId);
        supplier.setUseYn("N");
        supplier.setTradeStatus("STOPPED");
        supplier.setUpdatedAt(LocalDateTime.now());
    }

    private Supplier findActiveSupplier(Long supplierId) {
        return supplierRepository.findBySupplierIdAndUseYn(supplierId, "Y")
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "공급업체를 찾을 수 없습니다."));
    }

    private void applyRequest(Supplier supplier, SupplierRequest request) {
        supplier.setSupplierName(request.supplierName().trim());
        supplier.setBusinessNumber(normalizeBusinessNumber(request.businessNumber()));
        supplier.setManager(blankToNull(request.manager()));
        supplier.setPhone(blankToNull(request.phone()));
        supplier.setEmail(blankToNull(request.email()));
        supplier.setAddress(blankToNull(request.address()));
        supplier.setTradeStatus(normalizeTradeStatus(request.tradeStatus()));
    }

    private void validateSupplierCode(String supplierCode, Long currentSupplierId) {
        String normalizedCode = blankToNull(supplierCode);

        if (normalizedCode == null) {
            return;
        }

        boolean duplicated = currentSupplierId == null
                ? supplierRepository.existsBySupplierCode(normalizedCode)
                : supplierRepository.existsBySupplierCodeAndSupplierIdNot(normalizedCode, currentSupplierId);

        if (duplicated) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 사용 중인 공급업체 코드입니다.");
        }
    }

    private void validateBusinessNumber(String businessNumber, Long currentSupplierId) {
        String normalizedBusinessNumber = normalizeBusinessNumber(businessNumber);

        if (normalizedBusinessNumber == null) {
            return;
        }

        if (normalizedBusinessNumber.length() != 10) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사업자등록번호는 숫자 10자리로 입력하세요.");
        }

        boolean duplicated = currentSupplierId == null
                ? supplierRepository.existsByBusinessNumber(normalizedBusinessNumber)
                : supplierRepository.existsByBusinessNumberAndSupplierIdNot(normalizedBusinessNumber, currentSupplierId);

        if (duplicated) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 등록된 사업자등록번호입니다.");
        }
    }

    private String normalizeTradeStatusForSearch(String tradeStatus) {
        if (!StringUtils.hasText(tradeStatus)) {
            return null;
        }

        String normalized = tradeStatus.trim();
        if ("전체".equals(normalized) || "ALL".equalsIgnoreCase(normalized)) {
            return null;
        }

        return normalizeTradeStatus(normalized);
    }

    private String normalizeTradeStatus(String tradeStatus) {
        if (!StringUtils.hasText(tradeStatus)) {
            return "ACTIVE";
        }

        String normalized = tradeStatus.trim();
        String upper = normalized.toUpperCase();

        if ("거래중".equals(normalized) || "ACTIVE".equals(upper)) {
            return "ACTIVE";
        }

        if ("거래중지".equals(normalized) || "STOPPED".equals(upper) || "INACTIVE".equals(upper)) {
            return "STOPPED";
        }

        throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 거래 상태입니다.");
    }

    private String normalizeBusinessNumber(String businessNumber) {
        String normalized = blankToNull(businessNumber);

        if (normalized == null) {
            return null;
        }

        String digits = normalized.replaceAll("[^0-9]", "");

        return StringUtils.hasText(digits) ? digits : null;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String createDefaultSupplierCode(Long supplierId) {
        return "SUP-" + String.format("%04d", supplierId);
    }
}
