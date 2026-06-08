package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.ApprovalHistory;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

}
