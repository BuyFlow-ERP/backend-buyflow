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
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "APPROVAL_HISTORY")
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_history_seq")
    @SequenceGenerator(
        name = "approval_history_seq",
        sequenceName = "SEQ_APPROVAL_HISTORY",
        allocationSize = 1
)
    @Column(name = "APPROVAL_ID")
    private Long approvalId;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "APPROVER_ID")
    private Long approverId;

    @Column(name = "APPROVAL_STATUS")
    private String approvalStatus;

    @Column(name = "COMMENT_TEXT")
    private String commentText;

    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

    @Column(name = "APPROVAL_STEP")
    private Integer approvalStep;
}