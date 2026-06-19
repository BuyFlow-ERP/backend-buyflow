package com.buyflow.erp.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "INSPECTION")
@NoArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "inspection_seq"
    )
    @SequenceGenerator(
        name = "inspection_seq",
        sequenceName = "SEQ_INSPECTION",
        allocationSize = 1
    )
    @Column(name = "INSPECTION_ID")
    private Long inspectionId;

    @Column(name = "RECEIPT_ITEM_ID")
    private Long receiptItemId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Users user;

    @Column(name = "INSPECTION_DATE")
    private LocalDate inspectionDate; // Date type => LocalDate로 매핑

    @Column(name = "INSPECTION_TYPE", length = 50)
    private String inspectionType;

    @Column(name = "QUANTITY")
    private Long quantity; // Number(10) 정수형 매핑

    @Column(name = "INSPECTION_RESULT", length = 20)
    private String inspectionResult;

    @Column(name = "DEFECT_QUANTITY")
    private Long defectQuantity;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt; // TimeStamp 매핑

    @Column(name= "DISPOSITION", length = 50)
    private String disposition;

}
