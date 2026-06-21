package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;

import java.util.List;

public interface ReceiptService {

    List<Receipt> getReceipts();

    Receipt getReceipt(Long receiptId);

    void saveReceipt(ReceiptDto.ReceiptCreateRequest request);

    ReceiptDto.PageResponse<ReceiptDto.ListResponse> searchReceipts(
            String activeTab,
            String orderNumber,
            String supplierKeyword,
            String itemKeyword,
            String warehouseName,
            String expectedFrom,
            String expectedTo,
            String status,
            int page,
            int size
    );

    ReceiptDto.FilterOptionsResponse getFilterOptions();

    ReceiptDto.SummaryResponse getSummary();
}