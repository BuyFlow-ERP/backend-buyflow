package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Repository.InspectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {
    private final InspectionRepository inspectionRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ReceiptRepository receiptRepository;

    private final StockRepository stockRepository;
    private final StockHistoryRepository StockHistoryRepository;
    
    @Override
    public List<Inspection> getInspections() {
        return inspectionRepository.findAll();
    }
    
    @Override
    public void saveInspection(
        InspectionDto.CreateRequest request) {
         
         if(inspectionRepository.existsByReceiptItemId(request.getReceiptItemId())) {
            throw new RuntimeException("이미 검수 완료된 입고입니다.");
         }
         
         // 검수 저장
         Inspection inspection = new Inspection();

         inspection.setReceiptItemId(request.getReceiptItemId());
         Users user = userRepository.findById(request.getInspectorId()).orElseThrow();
         inspection.setUser(user);
         inspection.setInspectionDate(request.getInspectionDate());
         inspection.setInspectionType(request.getInspectionType());
         inspection.setQuantity(request.getQuantity());
         inspection.setDefectQuantity(request.getDefectQuantity());
         inspection.setInspectionResult(request.getInspectionResult());
         inspection.setNotes(request.getNotes());
         inspection.setCreatedAt(LocalDateTime.now());
         inspectionRepository.save(inspection);

         // 양품 수량 계산
         Long defectQty = request.getDefectQuantity() == null ? 0L : request.getDefectQuantity();
         // Long acceptedQty = request.getQuantity() - defectQty;

         // 입고이력 조회
         ReceipItem receiptItem = receiptItemRepository.findById(request.getReceiptItemId()).orElseThrow();

         // 입고 조회
         Receipt receipt = receiptRepository.findById(receiptItem.getReceiptId()).orElseThrow();
         String warehouseCode = receipt.getWarehouseCode();

         // 재고 조회
            Stock stock = stockRepository.findByProductIdAndWarehouseCode(receiptItem.getProductId(), warehouseCode).orElseThrow(() -> new RuntimeException("재고가 존재하지 않습니다."));
            Long beforeQty = stock.getQuantity().longValue();

            if(stock.getQuantity() < defectQty) {
               throw new RuntimeException("불량 수량이 현재 재고보다 많습니다.");
            }

            stock.setQuantity(stock.getQuantity() - defectQty.intValue());
         // Stock stock = stockRepository.findByProductIdAndWarehouseCode(receiptItem.getProductId(), warehouseCode).orElseThrow(null);
         // Long beforeQty = 0L;

         // if(stock != null && stock.getQuantity() != null) {
         //    beforeQty = stock.getQuantity().longValue();
         // }

         // 재고 반영
         // if(stock == null) {

         //    stock = new Stock();

         //    stock.setProductId(receiptItem.getProductId());

         //    stock.setWarehouseCode(warehouseCode);

         //    stock.setQuantity(acceptedQty.intValue());
         // } else {

         //    stock.setQuantity(stock.getQuantity() + acceptedQty.intValue());

         // }

            stock.setUpdateAt(LocalDateTime.now());

            stockRepository.save(stock);

            // 재고이력 생성
            StockHistory history = new StockHistory();

            history.setStockId(stock.getStockId());
            history.setHistoryType("INSPECTION_DEFECT");
            history.setChangeQty(acceptedQty);
            history.setBeforeQty(beforeQty);
            history.setAfterQty(stock.getQuantity().longValue());
            history.setRelatedReceiptItemId(receiptItem.getReceiptItemId());
            history.setReason("검수 불량 차감");
            // hisotry.setReason("검수 완료");
            hisotry.setCreatedAt(LocalDateTime.now());
            history.setCreatedBy(request.getUser().getUserId());
            
            StockHistoryRepository.save(history);
    }

}
