package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PurchaseOrderItemDto;
import com.buyflow.erp.Entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderItemService {

    List<PurchaseOrderItem> getOrderItems();

    void saveOrderItem(
            PurchaseOrderItemDto.CreateRequest request);
}