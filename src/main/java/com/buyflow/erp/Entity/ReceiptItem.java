package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "RECEIPT_ITEM")
public class ReceiptItem {

   @Id
@GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "receipt_item_seq"
)
@SequenceGenerator(
        name = "receipt_item_seq",
        sequenceName = "SEQ_RECEIPT_ITEM",
        allocationSize = 1
)
@Column(name = "RECEIPT_ITEM_ID")
private Long receiptItemId;

    @Column(name = "RECEIPT_ID")
    private Long receiptId;

    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "RECEIPT_QTY")
    private Long receiptQty;

    @Column(name = "DEFECT_QTY")
    private Long defectQty;

    @Column(name = "ACCEPTED_QTY")
    private Long acceptedQty;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "LOGIN_ID")
    private String loginId;
}