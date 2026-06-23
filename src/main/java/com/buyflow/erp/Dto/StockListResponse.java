package com.buyflow.erp.Dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockListResponse {

    private List<StockDto> items;

    private Pagination pagination;

    private Summary summary;

    @Getter
    @Builder
    public static class Pagination {

        private Integer page;

        private Integer size;

        private Long totalElements;

        private Integer totalPages;
    }

    @Getter
    @Builder
    public static class Summary {

        private Integer total;

        private Integer normal;

        private Integer low;

        private Integer outOfStock;
    }
}