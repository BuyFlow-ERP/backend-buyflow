package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SUPPLIER")
@SequenceGenerator(
        name = "SUPPLIER_SEQ_GENERATOR",
        sequenceName = "SEQ_SUPPLIER",
        allocationSize = 1
)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPLIER_SEQ_GENERATOR")
    @Column(name = "SUPPLIER_ID")
    private Long supplierId;

    @Column(name = "SUPPLIER_CODE", length = 50, unique = true)
    private String supplierCode;

    @Column(name = "SUPPLIER_NAME", length = 100, nullable = false)
    private String supplierName;

    @Column(name = "BUSINESS_NUMBER", length = 50)
    private String businessNumber;

    @Column(name = "MANAGER", length = 100)
    private String manager;

    @Column(name = "CONTACT", length = 50)
    private String phone;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "TRADE_STATUS", length = 20, nullable = false)
    private String tradeStatus = "ACTIVE";

    @Column(name = "USE_YN", length = 1, nullable = false)
    private String useYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
