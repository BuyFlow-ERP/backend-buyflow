package com.buyflow.erp.Entity;

import java.math.BigDecimal;
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
@Table(name = "PURCHASE_REQUEST_ITEM")
public class PurchaseRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_request_item_seq")
    @SequenceGenerator(
        name = "purchase_request_item_seq",
        sequenceName = "SEQ_PURCHASE_REQUEST_ITEM",
        allocationSize = 1
)
    @Column(name = "REQUEST_ITEM_ID")
    private Long requestItemId;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "REQUEST_QUANTITY")
    private Integer requestQuantity;

    @Column(name = "ESTIMATED_UNIT_PRICE", precision = 12, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}