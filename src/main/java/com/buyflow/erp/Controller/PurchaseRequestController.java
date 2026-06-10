package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Service.PurchaseRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {

	private final PurchaseRequestService purchaseRequestService;

	@GetMapping
	public ApiResponse<List<PurchaseRequestDto>> findAll() {
		return ApiResponse.success("Purchase request list loaded", purchaseRequestService.findAll());
	}
}
