package com.buyflow.erp.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "PURCHASE_REQUESTS")
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_request_seq")
    @SequenceGenerator(
        name = "purchase_request_seq",
        sequenceName = "SEQ_PURCHASE_REQUEST",
        allocationSize = 1
)
    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "REQUEST_NO")
    private String requestNo;

    @Column(name = "REQUESTOR_ID")
    private Long requestorId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "PRIORITY", length = 20)
    private String priority;

    @Column(name = "REQUEST_STATUS")
    private String requestStatus;

    @Column(name = "DELETED_YN", columnDefinition = "CHAR(1)")
    private String deletedYn = "N";
}