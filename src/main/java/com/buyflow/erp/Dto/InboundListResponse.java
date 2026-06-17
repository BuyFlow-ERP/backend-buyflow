package com.buyflow.erp.Dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InboundListResponse {

    private List<InboundDto> items;

    private Pagination pagination;

    @Getter
    @Builder
    public static class Pagination {

        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
    }
}