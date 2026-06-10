package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.PurchaseRequestItemDto;
import com.buyflow.erp.Service.PurchaseRequestItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-request-items")
public class PurchaseRequestItemController {
	
	private final PurchaseRequestItemService purchaseRequestItemService;

	@GetMapping
	public ApiResponse<List<PurchaseRequestItemDto>> findAll() {
		return ApiResponse.success("Purchase request item list loaded", purchaseRequestItemService.findAll());
	}
}
