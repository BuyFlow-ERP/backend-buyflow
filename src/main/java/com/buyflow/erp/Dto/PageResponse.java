package com.buyflow.erp.Dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
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