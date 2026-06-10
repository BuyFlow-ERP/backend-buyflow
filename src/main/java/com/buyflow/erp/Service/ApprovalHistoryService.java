package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.ApprovalHistoryDto;

public interface ApprovalHistoryService {

	List<ApprovalHistoryDto> findAll();
}
