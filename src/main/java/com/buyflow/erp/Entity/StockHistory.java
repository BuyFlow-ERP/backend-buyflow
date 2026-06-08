package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @Column(name = "HISTORY_ID")
    private Long historyId;

    @Column(name = "STOCK_ID", nullable = false)
    private Long stockId;

    @Column(name = "HISTORY_TYPE", length = 20)
    private String historyType;

    @Column(name = "CHANGE_QTY")
    private Long changeQty;

    @Column(name = "BEFOREQTY")
    private Long beforeQty;

    @Column(name = "AFTERQTY")
    private Long afterQty;

    @Column(name = "RELATED_RECEIPT_ID")
    private Long relatedReceiptId;

    @Column(name = "RELATED_ORDER_ITEM_ID")
    private Long relatedOrderItemId;

    @Column(name = "REASON", length = 100)
    private String reason;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public StockHistory(Long stockId, Long changeQty) {
        this.stockId = stockId;
        this.changeQty = changeQty;
        this.createdAt = LocalDateTime.now();
    }
    
}
