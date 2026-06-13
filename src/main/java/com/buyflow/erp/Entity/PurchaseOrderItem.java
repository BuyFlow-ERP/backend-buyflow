package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PURCHASE_ORDER_ITEM")
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

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "QUANTITY"m nullable = false)
    private Long quantity;

    @Column(name = "UNIT_PRICE", precision = 12, scale = 2, nullable = false)
    private Double unitPrice;
}