package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl
        implements ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Override
    public List<Receipt> getReceipts() {
        return receiptRepository.findAll();
    }

  @Override
public Receipt getReceipt(Long receiptId) {

    return receiptRepository.findById(receiptId)
            .orElseThrow();
}

@Override
public void saveReceipt(
    ReceiptDto.ReceiptCreateRequest request) {

    try {

        System.out.println("1. saveReceipt 시작");

        Receipt receipt = new Receipt();

        receipt.setOrderId(request.getOrderId());
        receipt.setWarehouseCode(request.getWarehouseCode());
        receipt.setReceiptNo(request.getReceiptNo());
        receipt.setReceiptDate(request.getReceiptDate());
        receipt.setReceiptStatus(request.getReceiptStatus());
        receipt.setLoginId(request.getLoginId());

        System.out.println("2. save 직전");

        receiptRepository.save(receipt);

        System.out.println("3. save 완료");

    } catch (Exception e) {

        e.printStackTrace();

        throw e;
    }
}
    }
