package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptItemDto;
import com.buyflow.erp.Entity.ReceiptItem;

import java.util.List;

public interface ReceiptItemService {

        List<ReceiptItem> getReceiptItems();

        void saveReceiptItem(
                        ReceiptItemDto.CreateRequest request);

        void updateReceiptItem(
                        Long receiptItemId,
                        ReceiptItemDto.CreateRequest request);

        void cancelReceiptItem(
                        Long receiptItemId);

        List<ReceiptItem> getReceiptItemsByStatus(
                        String receiptItemStatus);
}