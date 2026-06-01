package com.buyflow.erp.Entity;

@Entity
@Getter
@Setter
// @Table(name = "INSPECTION")
// @NoArgsConstructor
public class Inspection {

    @Id
    @Column(name = "INSPECTION_ID")
    private Long inspectionId;

    @Column(name = "RECEIPT_TIEM_ID", nullable = false)
    private Long receiptItemId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

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

    @Column(name = "LOGIN_ID", length = 50)
    private String loginId;


    
}
