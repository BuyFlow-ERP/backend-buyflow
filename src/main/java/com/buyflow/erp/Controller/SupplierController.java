package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.SupplierFilterOptionsResponse;
import com.buyflow.erp.Dto.SupplierPageResponse;
import com.buyflow.erp.Dto.SupplierRequest;
import com.buyflow.erp.Dto.SupplierResponse;
import com.buyflow.erp.Dto.SupplierTradeStatusRequest;
import com.buyflow.erp.Service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suppliers")
public class SupplierController {

    private static final String SUPPLIER_READ_AUTHORITY =
            "hasRole('ADMIN') or hasAuthority('SUPPLIER_READ') or hasAuthority('SUPPLIER_MANAGE')";
    private static final String SUPPLIER_MANAGE_AUTHORITY =
            "hasRole('ADMIN') or hasAuthority('SUPPLIER_MANAGE')";

    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize(SUPPLIER_READ_AUTHORITY)
    public ApiResponse<SupplierPageResponse> search(
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String manager,
            @RequestParam(required = false) String tradeStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ApiResponse.success(
                "Supplier list loaded.",
                supplierService.search(supplierCode, supplierName, manager, tradeStatus, page, size)
        );
    }

    @GetMapping("/filter-options")
    @PreAuthorize(SUPPLIER_READ_AUTHORITY)
    public ApiResponse<SupplierFilterOptionsResponse> findFilterOptions() {
        return ApiResponse.success("Supplier filter options loaded.", supplierService.findFilterOptions());
    }

    @GetMapping("/business-number/exists")
    @PreAuthorize(SUPPLIER_READ_AUTHORITY)
    public ApiResponse<Boolean> existsBusinessNumber(
            @RequestParam String businessNumber,
            @RequestParam(required = false) Long excludeSupplierId
    ) {
        return ApiResponse.success(
                "Supplier business number duplication checked.",
                supplierService.existsBusinessNumber(businessNumber, excludeSupplierId)
        );
    }

    @GetMapping("/{supplierId}")
    @PreAuthorize(SUPPLIER_READ_AUTHORITY)
    public ApiResponse<SupplierResponse> findById(@PathVariable Long supplierId) {
        return ApiResponse.success("Supplier detail loaded.", supplierService.findById(supplierId));
    }

    @PostMapping
    @PreAuthorize(SUPPLIER_MANAGE_AUTHORITY)
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ApiResponse.success("Supplier created.", supplierService.create(request));
    }

    @PutMapping("/{supplierId}")
    @PreAuthorize(SUPPLIER_MANAGE_AUTHORITY)
    public ApiResponse<SupplierResponse> update(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierRequest request
    ) {
        return ApiResponse.success("Supplier updated.", supplierService.update(supplierId, request));
    }

    @PatchMapping("/{supplierId}/trade-status")
    @PreAuthorize(SUPPLIER_MANAGE_AUTHORITY)
    public ApiResponse<SupplierResponse> changeTradeStatus(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierTradeStatusRequest request
    ) {
        return ApiResponse.success(
                "Supplier trade status changed.",
                supplierService.changeTradeStatus(supplierId, request.tradeStatus())
        );
    }

    @DeleteMapping("/{supplierId}")
    @PreAuthorize(SUPPLIER_MANAGE_AUTHORITY)
    public ApiResponse<Void> delete(@PathVariable Long supplierId) {
        supplierService.delete(supplierId);
        return ApiResponse.success("Supplier deleted.");
    }
}
