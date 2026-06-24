package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.InventoryAdjustmentRequest;
import com.buyflow.erp.Dto.InventoryAdjustmentResponse;

public interface InventoryService {

    InventoryAdjustmentResponse adjustStock(Long stockId, InventoryAdjustmentRequest request);

    void increaseStock(
            Long productId,
            String warehouseCode,
            int quantity);
}