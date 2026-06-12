package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PURCHASE_REQUEST_ITEM")
public class PurchaseRequestItem {

    @Id
    @Column(name = "REQUEST_ITEM_ID")
    private Long requestItemId;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "REQUEST_QUANTITY")
    private int requestQuantity;

    @Column(name = "ESTIMATED_UNIT_PRICE")
    private int estimatedUnitPrice;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}