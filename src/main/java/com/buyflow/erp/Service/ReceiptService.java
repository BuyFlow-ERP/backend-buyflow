package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;

import java.util.List;

public interface ReceiptService {

    List<Receipt> getReceipts();

    Receipt getReceipt(Long receiptId);

    void saveReceipt(ReceiptDto.ReceiptCreateRequest request
);
}