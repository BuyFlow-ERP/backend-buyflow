package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "STOCK")
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @Column(name = "STOCK_ID")
    private Long stockId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "WAREHOUSE_CODE", length = 50, nullable = false)
    private String warehouseCode;

    @Column(name = "QUANTITY", precision = 10)
    private Integer quantity;

    @Column(name = "SAFETY_STOCK")
    private Integer safetyStock;

    @Column(name = "STOCK_STATUS", length = 20)
    private String stockStatus;

    @Column(name = "LOT_NO", length = 50)
    private String lotNo;
    
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
