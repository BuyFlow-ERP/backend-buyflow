package com.buyflow.erp.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.buyflow.erp.Dto.InboundDto;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Repository.PurchaseOrderRepository;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import com.buyflow.erp.Repository.ReceiptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final ReceiptRepository receiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ReceiptItemRepository receiptItemRepository;

    @Override
    public List<InboundDto> getInbounds() {

        List<InboundDto> result = new ArrayList<>();

        List<Receipt> receipts = receiptRepository.findAll();

        for (Receipt receipt : receipts) {

            PurchaseOrder order =
                    purchaseOrderRepository.findById(
                            receipt.getOrderId())
                    .orElse(null);

            if (order == null) {
                continue;
            }

            Long orderQty = 0L;

            for (PurchaseOrderItem item : order.getItems()) {
                orderQty += item.getQuantity();
            }

            Long receivedQty = 0L;

            List<ReceiptItem> receiptItems =
                    receiptItemRepository.findByReceiptId(
                            receipt.getReceiptId());

            for (ReceiptItem item : receiptItems) {

                if (item.getAcceptedQty() != null) {
                    receivedQty += item.getAcceptedQty();
                }
            }

            result.add(
                    InboundDto.builder()
                            .id(receipt.getReceiptId())
                            .orderNumber(
                                    "PO-2026-"
                                    + String.format("%04d",
                                            order.getOrderId()))
                            .supplierName(
                                    order.getSupplier() != null
                                            ? order.getSupplier()
                                                    .getSupplierName()
                                            : "-")
                            .orderedAt(
                                    order.getCreatedAt() != null
                                            ? order.getCreatedAt()
                                                    .toLocalDate()
                                                    .toString()
                                            : "")
                            .expectedInboundAt(
                                    order.getDueDate() != null
                                            ? order.getDueDate()
                                                    .toLocalDate()
                                                    .toString()
                                            : "")
                            .warehouseName(
                                    receipt.getWarehouseCode())
                            .itemCount(
                                    order.getItems().size())
                            .orderQuantity(
                                    orderQty)
                            .receivedQuantity(
                                    receivedQty)
                            .remainingQuantity(
                                    orderQty - receivedQty)
                            .status(
                                    receipt.getReceiptStatus())
                            .build()
            );
        }

        return result;
    }
}