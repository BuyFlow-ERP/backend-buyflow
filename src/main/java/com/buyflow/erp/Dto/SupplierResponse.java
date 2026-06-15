package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.Supplier;

import java.time.LocalDateTime;

public record SupplierResponse(
        Long id,
        Long supplierId,
        String code,
        String supplierCode,
        String name,
        String supplierName,
        String businessNumber,
        String manager,
        String phone,
        String email,
        String address,
        String tradeStatus,
        String tradeStatusCode,
        String useYn,
        String registeredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SupplierResponse from(Supplier supplier) {
        String tradeStatusCode = supplier.getTradeStatus() == null ? "ACTIVE" : supplier.getTradeStatus();
        String tradeStatusLabel = switch (tradeStatusCode) {
            case "STOPPED", "INACTIVE" -> "거래중지";
            default -> "거래중";
        };

        String registeredAt = supplier.getCreatedAt() == null
                ? ""
                : supplier.getCreatedAt().toLocalDate().toString();

        return new SupplierResponse(
                supplier.getSupplierId(),
                supplier.getSupplierId(),
                supplier.getSupplierCode(),
                supplier.getSupplierCode(),
                supplier.getSupplierName(),
                supplier.getSupplierName(),
                supplier.getBusinessNumber(),
                supplier.getManager(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress(),
                tradeStatusLabel,
                tradeStatusCode,
                supplier.getUseYn(),
                registeredAt,
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}
