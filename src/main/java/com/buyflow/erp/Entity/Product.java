package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PRODUCTS")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
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
}
