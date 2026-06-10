package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.PurchaseRequestDto;
import com.buyflow.erp.Entity.PurchaseRequest;
import com.buyflow.erp.Repository.PurchaseRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

	private final PurchaseRequestRepository purchaseRequestRepository;

	@Override
	public List<PurchaseRequestDto> findAll() {
		return purchaseRequestRepository.findAll()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	private PurchaseRequestDto toDto(PurchaseRequest purchaseRequest) {
		PurchaseRequestDto dto = new PurchaseRequestDto();
		dto.setRequestId(purchaseRequest.getRequestId());
		dto.setRequestNo(purchaseRequest.getRequestNo());
		dto.setRequestorId(purchaseRequest.getRequestorId());
		dto.setTitle(purchaseRequest.getTitle());
		dto.setReason(purchaseRequest.getReason());
		dto.setDueDate(purchaseRequest.getDueDate());
		dto.setTotalAmount(purchaseRequest.getTotalAmount());
		dto.setCreatedAt(purchaseRequest.getCreatedAt());
		dto.setUpdatedAt(purchaseRequest.getUpdatedAt());
		dto.setRequestStatus(purchaseRequest.getRequestStatus());
		dto.setDeletedYn(purchaseRequest.getDeletedYn());
		return dto;
	}
}
