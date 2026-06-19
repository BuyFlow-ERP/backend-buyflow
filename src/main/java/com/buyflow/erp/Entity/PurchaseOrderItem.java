package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PURCHASE_ORDER_ITEM")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "purchase_order_item_seq"
    )
    @SequenceGenerator(
            name = "purchase_order_item_seq",
            sequenceName = "SEQ_PURCHASE_ORDER_ITEM",
            allocationSize = 1
    )
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private PurchaseOrder purchaseOrder;
    
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "QUANTITY", nullable = false)
    private Long quantity;

    @Column(name = "UNIT_PRICE", nullable = false)
    private Double unitPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    private PurchaseRequest purchaseRequest;
    
}