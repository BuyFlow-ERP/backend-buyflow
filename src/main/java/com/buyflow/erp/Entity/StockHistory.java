package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "STOCK_HISTORY")
@NoArgsConstructor
public class StockHistory {

@Id
@GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "stock_history_seq"
)
@SequenceGenerator(
        name = "stock_history_seq",
        sequenceName = "SEQ_STOCK_HISTORY",
        allocationSize = 1
)
@Column(name = "HISTORY_ID")
private Long historyId;

    @Column(name = "STOCK_ID", nullable = false)
    private Long stockId;

    @Column(name = "HISTORY_TYPE", length = 20)
    private String historyType;

    @Column(name = "CHANGE_QTY")
    private Long changeQty;

@Column(name = "BEFORE_QTY")
private Long beforeQty;

@Column(name = "AFTER_QTY")
private Long afterQty;

@Column(name = "RELATED_RECEIPT_ITEM_ID")
private Long relatedReceiptItemId;

    @Column(name = "RELATED_ORDER_ITEM_ID")
    private Long relatedOrderItemId;

    @Column(name = "REASON", length = 100)
    private String reason;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY")
private String createdBy;

    public StockHistory(Long stockId, Long changeQty) {
        this.stockId = stockId;
        this.changeQty = changeQty;
        this.createdAt = LocalDateTime.now();
    }
    
}
