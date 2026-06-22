package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "PRODUCTS")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_NO")
    private String productNo;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "UNIT_PRICE")
    private Long unitPrice;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Lob
    @Column(name = "SPEC")
    private String spec;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "USE_YN")
    private String useYn = "Y";

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.updatedAt == null) {
            this.updatedAt = now;
        }

        if (this.useYn == null || this.useYn.isBlank()) {
            this.useYn = "Y";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.useYn == null || this.useYn.isBlank()) {
            this.useYn = "Y";
        }
    }
}