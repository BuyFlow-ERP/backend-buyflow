package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ReceiptItemDto;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.ReceiptRepository;
import com.buyflow.erp.Entity.PurchaseOrderItem;

import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Repository.StockHistoryRepository;

@Service
@RequiredArgsConstructor
public class ReceiptItemServiceImpl
                implements ReceiptItemService {

        private final ReceiptRepository receiptRepository;
        private final StockRepository stockRepository;
        private final ReceiptItemRepository receiptItemRepository;
        private final PurchaseOrderItemRepository purchaseOrderItemRepository;
        private final StockHistoryRepository stockHistoryRepository;

        @Override
        public List<ReceiptItem> getReceiptItems() {

                return receiptItemRepository.findAll();
        }

        @Override
        public List<ReceiptItem> getReceiptItemsByStatus(
                        String receiptItemStatus) {

                return receiptItemRepository
                                .findByReceiptItemStatus(
                                                receiptItemStatus);
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
                                receiptQty - defectQty);

                item.setRemark(request.getRemark());
                item.setLoginId(request.getLoginId());
                item.setCreatedAt(LocalDateTime.now());
                item.setReceiptItemStatus(
                                "ACTIVE");

                receiptItemRepository.save(item);
                Receipt receipt = receiptRepository.findById(
                                request.getReceiptId()).orElseThrow();

                String warehouseCode = receipt.getWarehouseCode();

                Stock stock = stockRepository
                                .findByProductIdAndWarehouseCode(
                                                item.getProductId(),
                                                warehouseCode)
                                .orElse(null);
                Integer beforeQty = 0;

                if (stock != null && stock.getQuantity() != null) {
                        beforeQty = stock.getQuantity();
                }

                if (stock == null) {

                        stock = new Stock();

                        stock.setProductId(
                                        item.getProductId());

                        stock.setWarehouseCode(
                                        warehouseCode);

                        stock.setQuantity(
                                        item.getAcceptedQty().intValue());

                } else {

                        stock.setQuantity(
                                        stock.getQuantity()
                                                        + item.getAcceptedQty().intValue());
                }

                stock.setUpdatedAt(
                                LocalDateTime.now());

                stockRepository.save(stock);

                StockHistory history = new StockHistory();

                history.setStockId(
                                stock.getStockId());

                history.setHistoryType(
                "INBOUND");

                history.setChangeQty(
                                item.getAcceptedQty());

                history.setBeforeQty(
                                beforeQty.longValue());

                history.setAfterQty(
                                stock.getQuantity().longValue());

                history.setRelatedReceiptItemId(
                                item.getReceiptItemId());

                history.setRelatedOrderItemId(
                                item.getOrderItemId());

                history.setReason(
                                "입고 처리");

                history.setCreatedAt(
                                LocalDateTime.now());

                history.setCreatedBy(
                                item.getLoginId());

                stockHistoryRepository.save(history);

                PurchaseOrderItem orderItem = purchaseOrderItemRepository
                                .findById(
                                                request.getOrderItemId())
                                .orElseThrow();

                Long orderedQty = orderItem.getQuantity();

                Long acceptedQtySum = receiptItemRepository.getAcceptedQtySum(
                                request.getOrderItemId());

                if (acceptedQtySum >= orderedQty) {

                        receipt.setReceiptStatus(
                                        "COMPLETED");

                } else {

                        receipt.setReceiptStatus(
                                        "PARTIAL");
                }

                receiptRepository.save(receipt);
        }

        @Override
        public void updateReceiptItem(
                        Long receiptItemId,
                        ReceiptItemDto.CreateRequest request) {

                ReceiptItem item = receiptItemRepository
                                .findById(receiptItemId)
                                .orElseThrow();

                Long oldAcceptedQty = item.getAcceptedQty();

                item.setReceiptQty(
                                request.getReceiptQty());

                item.setDefectQty(
                                request.getDefectQty());

                Long defectQty = request.getDefectQty() == null
                                ? 0L
                                : request.getDefectQty();

                item.setAcceptedQty(
                                request.getReceiptQty() - defectQty);

                Long newAcceptedQty = item.getAcceptedQty();

                Long qtyDiff = newAcceptedQty - oldAcceptedQty;

                item.setRemark(
                                request.getRemark());

                Receipt receipt = receiptRepository
                                .findById(
                                                item.getReceiptId())
                                .orElseThrow();

                String warehouseCode = receipt.getWarehouseCode();

                Stock stock = stockRepository
                                .findByProductIdAndWarehouseCode(
                                                item.getProductId(),
                                                warehouseCode)
                                .orElseThrow();

                stock.setQuantity(
                                stock.getQuantity()
                                                + qtyDiff.intValue());

                stock.setUpdatedAt(
                                LocalDateTime.now());

                stockRepository.save(stock);

                Long beforeQty = stock.getQuantity().longValue()
                                - qtyDiff;

                StockHistory history = new StockHistory();

                history.setStockId(
                                stock.getStockId());

                history.setHistoryType(
                                "UPDATE");

                history.setChangeQty(
                                qtyDiff);

                history.setBeforeQty(
                                beforeQty);

                history.setAfterQty(
                                stock.getQuantity().longValue());

                history.setRelatedReceiptItemId(
                                item.getReceiptItemId());

                history.setRelatedOrderItemId(
                                item.getOrderItemId());

                history.setReason(
                                "입고 수정");

                history.setCreatedAt(
                                LocalDateTime.now());

                history.setCreatedBy(
                                item.getLoginId());

                stockHistoryRepository.save(
                                history);

                receiptItemRepository.save(item);
                PurchaseOrderItem orderItem = purchaseOrderItemRepository
                                .findById(
                                                item.getOrderItemId())
                                .orElseThrow();

                Long orderedQty = orderItem.getQuantity();

                Long acceptedQtySum = receiptItemRepository.getAcceptedQtySum(
                                item.getOrderItemId());

                if (acceptedQtySum >= orderedQty) {

                        receipt.setReceiptStatus(
                                        "COMPLETED");

                } else {

                        receipt.setReceiptStatus(
                                        "PARTIAL");
                }

                receiptRepository.save(receipt);
        }

        @Override
        public void cancelReceiptItem(
                        Long receiptItemId) {

                ReceiptItem item = receiptItemRepository
                                .findById(receiptItemId)
                                .orElseThrow();

                if ("CANCELLED".equals(
                                item.getReceiptItemStatus())) {
                        return;
                }

                Receipt receipt = receiptRepository
                                .findById(item.getReceiptId())
                                .orElseThrow();

                String warehouseCode = receipt.getWarehouseCode();

                Stock stock = stockRepository
                                .findByProductIdAndWarehouseCode(
                                                item.getProductId(),
                                                warehouseCode)
                                .orElseThrow();

                Long acceptedQty = item.getAcceptedQty();

                Long beforeQty = stock.getQuantity().longValue();

                stock.setQuantity(
                                stock.getQuantity()
                                                - acceptedQty.intValue());

                stock.setUpdatedAt(
                                LocalDateTime.now());

                stockRepository.save(stock);

                StockHistory history = new StockHistory();

                history.setStockId(
                                stock.getStockId());

                history.setHistoryType(
                                "CANCEL");

                history.setChangeQty(
                                -acceptedQty);

                history.setBeforeQty(
                                beforeQty);

                history.setAfterQty(
                                stock.getQuantity().longValue());

                history.setRelatedReceiptItemId(
                                item.getReceiptItemId());

                history.setRelatedOrderItemId(
                                item.getOrderItemId());

                history.setReason(
                                "입고 취소");

                history.setCreatedAt(
                                LocalDateTime.now());

                history.setCreatedBy(
                                item.getLoginId());

                stockHistoryRepository.save(
                                history);

                item.setReceiptItemStatus(
                                "CANCELLED");

                receiptItemRepository.save(
                                item);

                PurchaseOrderItem orderItem = purchaseOrderItemRepository
                                .findById(
                                                item.getOrderItemId())
                                .orElseThrow();

                Long orderedQty = orderItem.getQuantity();

                Long acceptedQtySum = receiptItemRepository.getAcceptedQtySum(
                                item.getOrderItemId());

                if (acceptedQtySum >= orderedQty) {

                        receipt.setReceiptStatus(
                                        "COMPLETED");

                } else {

                        receipt.setReceiptStatus(
                                        "PARTIAL");
                }

                receiptRepository.save(
                                receipt);
        }
}