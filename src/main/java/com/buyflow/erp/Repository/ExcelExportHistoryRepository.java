package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.ExcelExportHistory;

@Repository
public interface ExcelExportHistoryRepository extends JpaRepository<ExcelExportHistory, Long> {

}
