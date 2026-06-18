package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "WAREHOUSE")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Warehouse {
    
    @Id
    @Column(name = "WAREHOUSE_CODE", length = 50)
    private String warehouseCode;

    @Column(name = "WAREHOUSE_NAME", length = 100)
    private String warehouseName;
 
    @Column(name = "ZIPCODE", length = 10)
    private String zipcode;

    @Column(name = "ADDRESS", length = 300)
    private String address;
    
    @Column(name = "DETAIL_ADDRESS", length = 200)
    private String detailAddress;
 
    @Column(name = "CONTACT", length = 50)
    private String contact;
 
    @Column(name = "USE_YN", length = 1)
    private String useYn = "Y";
 
    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
 
    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    
    @Column(name = "TYPE", length = 50)
    private String type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Users user;
    
    @Column(name = "MEMO")
    private String memo;
}
