package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findAllByOrderByApprovalIdDesc();

    List<ApprovalHistory> findByRequestIdOrderByApprovalStepAsc(Long requestId);
}