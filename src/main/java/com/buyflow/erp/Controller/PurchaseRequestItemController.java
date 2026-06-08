package com.buyflow.erp.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Service.PurchaseRequestItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchaseRequestItemController {
	
	private final PurchaseRequestItemService purchaseRequestItemService;

}
