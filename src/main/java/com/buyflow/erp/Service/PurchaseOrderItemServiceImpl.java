package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Repository.ProductRepository;
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
	private final ProductRepository productRepository;

	@Override
	public List<PurchaseOrderItem> getOrderItems() {

		return purchaseOrderItemRepository.findAll();

	}

	@Override
	public void saveOrderItem(PurchaseOrderItemDto.CreateRequest request) {
		
		PurchaseOrder order = purchaseOrderRepository.findById(request.getOrderId())
				.orElseThrow(()-> new EntityNotFoundException("발주서가 없습니다."));

		Product product = productRepository.findById(request.getProductId())   // ← 수정
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + request.getProductId()));
		
		PurchaseOrderItem item = PurchaseOrderItem.builder()
				.purchaseOrder(order)
				.product(product)
				.quantity(request.getQuantity())
				.unitPrice(request.getUnitPrice())
				.build();

		purchaseOrderItemRepository.save(item);
	}
}