package com.buyflow.erp.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Service.PurchaseRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchaseRequestController {

	private final PurchaseRequestService purchaseRequestService;
}
