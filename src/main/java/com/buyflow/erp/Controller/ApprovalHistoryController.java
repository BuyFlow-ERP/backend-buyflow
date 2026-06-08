package com.buyflow.erp.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Service.ApprovalHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApprovalHistoryController {
	
	private final ApprovalHistoryService approvalHistoryService;

}
