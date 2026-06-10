package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.PurchaseRequestDto;

public interface PurchaseRequestService {

	List<PurchaseRequestDto> findAll();
}
