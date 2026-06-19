package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findAllByOrderByApprovalIdDesc();

    List<ApprovalHistory> findByRequestIdOrderByApprovalStepAsc(Long requestId);

    Optional<ApprovalHistory> findFirstByRequestIdAndApprovalStatusInOrderByApprovalStepDesc(
            Long requestId,
            Collection<String> approvalStatuses
    );
}