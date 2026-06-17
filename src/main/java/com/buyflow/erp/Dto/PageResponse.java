package com.buyflow.erp.Dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageResponse<T>(
        List<T> items,
        Pagination pagination
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new Pagination(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalElements(),
                        Math.max(page.getTotalPages(), 1)
                )
        );
    }

    @JsonProperty("content")
    public List<T> content() {
        return items;
    }

    @JsonProperty("page")
    public int page() {
        return pagination.page();
    }

    @JsonProperty("size")
    public int size() {
        return pagination.size();
    }

    @JsonProperty("totalElements")
    public long totalElements() {
        return pagination.totalElements();
    }

    @JsonProperty("totalPages")
    public int totalPages() {
        return pagination.totalPages();
    }

    @JsonProperty("first")
    public boolean first() {
        return pagination.page() <= 1;
    }

    @JsonProperty("last")
    public boolean last() {
        return pagination.page() >= pagination.totalPages();
    }

    public record Pagination(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }
}
