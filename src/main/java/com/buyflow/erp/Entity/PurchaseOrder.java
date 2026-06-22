package com.buyflow.erp.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PURCHASE_ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_seq")
    @SequenceGenerator(name = "purchase_order_seq", sequenceName = "SEQ_PURCHASE_ORDER", allocationSize = 1)
    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID", nullable = false)
    private Supplier supplier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @Column(name = "ORDER_STATUS", length = 50)
    private String orderStatus;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;
    
    @Column(name = "ORDER_NO", length = 50, nullable = false, unique = true)
    private String orderNo;
    
    @Column(name = "MEMO")
    private String memo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    private PurchaseRequest purchaseRequest;
    
    @Column(name = "CREATED_BY")
    private Long createdBy;
    
    @Column(name = "EXPECTED_RECEIPT_FROM")
    private LocalDate expectedReceiptFrom;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderItem> items = new ArrayList<>();

    // 편의 메서드
    public void addItem(PurchaseOrderItem item) {
        this.items.add(item);
        item.setPurchaseOrder(this);
    }
}
