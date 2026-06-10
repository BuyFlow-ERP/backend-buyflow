package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.ApprovalHistoryDto;
import com.buyflow.erp.Service.ApprovalHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval-history")
public class ApprovalHistoryController {
	
	private final ApprovalHistoryService approvalHistoryService;

	@GetMapping
	public ApiResponse<List<ApprovalHistoryDto>> findAll() {
		return ApiResponse.success("Approval history list loaded", approvalHistoryService.findAll());
	}
}
