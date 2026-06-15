package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.SupplierFilterOptionsResponse;
import com.buyflow.erp.Dto.SupplierPageResponse;
import com.buyflow.erp.Dto.SupplierRequest;
import com.buyflow.erp.Dto.SupplierResponse;
import com.buyflow.erp.Service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
@PreAuthorize("isAuthenticated()")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
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
    public ApiResponse<SupplierFilterOptionsResponse> findFilterOptions() {
        return ApiResponse.success("Supplier filter options loaded.", supplierService.findFilterOptions());
    }

    @GetMapping("/{supplierId}")
    public ApiResponse<SupplierResponse> findById(@PathVariable Long supplierId) {
        return ApiResponse.success("Supplier detail loaded.", supplierService.findById(supplierId));
    }

    @PostMapping
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ApiResponse.success("Supplier created.", supplierService.create(request));
    }

    @PutMapping("/{supplierId}")
    public ApiResponse<SupplierResponse> update(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierRequest request
    ) {
        return ApiResponse.success("Supplier updated.", supplierService.update(supplierId, request));
    }

    @DeleteMapping("/{supplierId}")
    public ApiResponse<Void> delete(@PathVariable Long supplierId) {
        supplierService.delete(supplierId);
        return ApiResponse.success("Supplier deleted.");
    }
}
