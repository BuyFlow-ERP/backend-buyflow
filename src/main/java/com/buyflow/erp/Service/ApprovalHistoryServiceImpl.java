package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Entity.ApprovalHistory;
import com.buyflow.erp.Repository.ApprovalHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService {

	private final ApprovalHistoryRepository approvalHistoryRepository;

	@Override
	public List<ApprovalHistoryDto> findAll() {
		return approvalHistoryRepository.findAll()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	private ApprovalHistoryDto toDto(ApprovalHistory approvalHistory) {
		ApprovalHistoryDto dto = new ApprovalHistoryDto();
		dto.setApprovalId(approvalHistory.getApprovalId());
		dto.setRequestId(approvalHistory.getRequestId());
		dto.setApproverId(approvalHistory.getApproverId());
		dto.setApprovalStatus(approvalHistory.getApprovalStatus());
		dto.setCommentText(approvalHistory.getCommentText());
		dto.setApprovedAt(approvalHistory.getApprovedAt());
		dto.setApprovalStep(approvalHistory.getApprovalStep());
		return dto;
	}
}
