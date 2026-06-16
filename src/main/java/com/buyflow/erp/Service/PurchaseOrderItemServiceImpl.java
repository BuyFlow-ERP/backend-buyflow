package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository;
import com.buyflow.erp.Repository.PurchaseOrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderItemServiceImpl implements PurchaseOrderItemService {

	private final PurchaseOrderItemRepository purchaseOrderItemRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;

	@Override
	public List<PurchaseOrderItem> getOrderItems() {

		return purchaseOrderItemRepository.findAll();

	}

	@Override
	public void saveOrderItem(PurchaseOrderItemDto.CreateRequest request) {

		PurchaseOrderItem item = new PurchaseOrderItem();
		
		PurchaseOrder order = purchaseOrderRepository.findById(request.getOrderId())
				.orElseThrow(()-> new EntityNotFoundException("발주서가 없습니다."));

		item.setPurchaseOrder(order);
		item.setProductId(request.getProductId());
		item.setQuantity(request.getQuantity());
		item.setUnitPrice(request.getUnitPrice());

		purchaseOrderItemRepository.save(item);
	}
}