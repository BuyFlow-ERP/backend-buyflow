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

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "UNIT_PRICE")
    private Double unitPrice;
}