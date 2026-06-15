package com.buyflow.erp.Dto;

import java.util.List;

public record SupplierPageResponse(
        List<SupplierResponse> items,
        Pagination pagination
) {
    public record Pagination(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }
}
