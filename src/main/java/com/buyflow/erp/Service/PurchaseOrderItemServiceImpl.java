package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderItemServiceImpl
        implements PurchaseOrderItemService {

    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Override
    public List<PurchaseOrderItem> getOrderItems() {

        return purchaseOrderItemRepository.findAll();

    }
        @Override
public void saveOrderItem(
        PurchaseOrderItemDto.CreateRequest request) {

    PurchaseOrderItem item = new PurchaseOrderItem();

    item.setOrderId(request.getOrderId());
    item.setProductId(request.getProductId());
    item.setQuantity(request.getQuantity());
    item.setUnitPrice(request.getUnitPrice());

    purchaseOrderItemRepository.save(item);
}
}