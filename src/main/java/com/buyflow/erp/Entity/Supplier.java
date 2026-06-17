package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_seq")
    @SequenceGenerator(name = "supplier_seq", sequenceName = "SEQ_SUPPLIER", allocationSize = 1)
    @Column(name = "SUPPLIER_ID")
    private Long supplierId;

    @Column(name = "SUPPLIER_CODE", length = 50)
    private String supplierCode;

    @Column(name = "SUPPLIER_NAME", length = 100)
    private String supplierName;

    @Column(name = "BUSINESS_NUMBER", length = 50)
    private String businessNumber;

    @Column(name = "CONTACT", length = 50)
    private String contact;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "MANAGER", length = 100)
    private String manager;

    @Column(name = "TRADE_STATUS", length = 20)
    private String tradeStatus = "ACTIVE";

    @Column(name = "USE_YN", length = 1)
    private String useYn = "Y";

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public String getPhone() {
        return contact;
    }

    public void setPhone(String phone) {
        this.contact = phone;
    }
}
