package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ReceiptService {

    List<Receipt> getReceipts();

    ReceiptDto.DetailResponse getReceipt(Long receiptId);

    ReceiptDto.DetailResponse getReceiptByOrderId(Long orderId);

    Long saveReceipt(ReceiptDto.ReceiptCreateRequest request, MultipartFile file);

    ReceiptDto.PageResponse<ReceiptDto.ListResponse> searchReceipts(
            String activeTab,
            String cardFilter,
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

    ReceiptDto.FormOptionsResponse getFormOptions();

    ReceiptDto.SummaryResponse getSummary();
}
