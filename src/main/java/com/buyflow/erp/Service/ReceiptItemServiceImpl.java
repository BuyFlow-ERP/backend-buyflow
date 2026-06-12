package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptItemDto;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.ReceiptRepository;

@Service
@RequiredArgsConstructor
public class ReceiptItemServiceImpl
        implements ReceiptItemService {
    
    private final ReceiptRepository receiptRepository;
    private final StockRepository stockRepository;
    private final ReceiptItemRepository receiptItemRepository;

    @Override
    public List<ReceiptItem> getReceiptItems() {

        return receiptItemRepository.findAll();
    }

    @Override
    public void saveReceiptItem(
            ReceiptItemDto.CreateRequest request) {

        ReceiptItem item = new ReceiptItem();

        item.setReceiptId(request.getReceiptId());
        item.setOrderItemId(request.getOrderItemId());
        item.setProductId(request.getProductId());

        item.setReceiptQty(request.getReceiptQty());
        item.setDefectQty(request.getDefectQty());
        Long receiptQty = request.getReceiptQty();
Long defectQty = request.getDefectQty();

if (defectQty == null) {
    defectQty = 0L;
}

item.setAcceptedQty(
        receiptQty - defectQty
);

        item.setRemark(request.getRemark());
        item.setLoginId(request.getLoginId());
        item.setCreatedAt(LocalDateTime.now());

        receiptItemRepository.save(item);
        Receipt receipt =
        receiptRepository.findById(
                request.getReceiptId()
        ).orElseThrow();

String warehouseCode =
        receipt.getWarehouseCode();

Stock stock =
        stockRepository
                .findByProductIdAndWarehouseCode(
                        item.getProductId(),
                        warehouseCode
                )
                .orElse(null);

if (stock == null) {

    stock = new Stock();

    stock.setProductId(
            item.getProductId()
    );

    stock.setWarehouseCode(
            warehouseCode
    );

    stock.setQuantity(
            item.getAcceptedQty().intValue()
    );

} else {

    stock.setQuantity(
            stock.getQuantity()
                    + item.getAcceptedQty().intValue()
    );
}

stock.setUpdatedAt(
        LocalDateTime.now()
);

stockRepository.save(stock);
    }
}