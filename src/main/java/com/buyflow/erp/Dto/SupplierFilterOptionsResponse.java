package com.buyflow.erp.Dto;

import java.util.List;

public record SupplierFilterOptionsResponse(
        List<String> tradeStatuses
) {
    public static SupplierFilterOptionsResponse defaults() {
        return new SupplierFilterOptionsResponse(List.of("전체", "거래중", "거래중지"));
    }
}
