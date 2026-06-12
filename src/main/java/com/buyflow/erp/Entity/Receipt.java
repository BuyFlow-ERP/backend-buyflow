package com.buyflow.erp.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "RECEIPT")
public class Receipt {

   @Id
@GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "receipt_seq"
)
@SequenceGenerator(
        name = "receipt_seq",
        sequenceName = "SEQ_RECEIPT",
        allocationSize = 1
)
@Column(name = "RECEIPT_ID")
private Long receiptId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "WAREHOUSE_CODE")
    private String warehouseCode;

    @Column(name = "INSPECTION_ID")
    private Long inspectionId;

    @Column(name = "RECEIPT_NO")
    private String receiptNo;

    @Column(name = "RECEIPT_DATE")
    private LocalDateTime receiptDate;

    @Column(name = "RECEIPT_STATUS")
    private String receiptStatus;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "LOGIN_ID")
    private String loginId;
}