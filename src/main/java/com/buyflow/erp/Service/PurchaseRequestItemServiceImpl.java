package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.PurchaseRequestItemDto;
import com.buyflow.erp.Entity.PurchaseRequestItem;
import com.buyflow.erp.Repository.PurchaseRequestItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseRequestItemServiceImpl implements PurchaseRequestItemService {

	private final PurchaseRequestItemRepository purchaseRequestItemRepository;

	@Override
	public List<PurchaseRequestItemDto> findAll() {
		return purchaseRequestItemRepository.findAll()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	private PurchaseRequestItemDto toDto(PurchaseRequestItem purchaseRequestItem) {
		PurchaseRequestItemDto dto = new PurchaseRequestItemDto();
		dto.setRequestItemId(purchaseRequestItem.getRequestItemId());
		dto.setRequestId(purchaseRequestItem.getRequestId());
		dto.setProductId(purchaseRequestItem.getProductId());
		dto.setRequestQuantity(purchaseRequestItem.getRequestQuantity());
		dto.setEstimatedUnitPrice(purchaseRequestItem.getEstimatedUnitPrice());
		dto.setRemark(purchaseRequestItem.getRemark());
		dto.setCreatedAt(purchaseRequestItem.getCreatedAt());
		dto.setUpdatedAt(purchaseRequestItem.getUpdatedAt());
		return dto;
	}
}
