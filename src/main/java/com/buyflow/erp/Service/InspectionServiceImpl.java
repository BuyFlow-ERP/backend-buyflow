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
@Transactional
public class InspectionServiceImpl implements InspectionService {
    private final InspectionRepository inspectionRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ReceiptRepository receiptRepository;

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final userRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Inspection> getInspections() {
        return inspectionRepository.findAll();
    }
    
    @Override
    public void saveInspection(InspectionDto.CreateRequest request) {
         
         // 중복 검수 예외 처리
         if(inspectionRepository.existsByReceiptItemId(request.getReceiptItemId())) {
            throw new RuntimeException("이미 검수 완료된 입고입니다.");
         }
         
         // 검수 정보 빌드 및 저장
         Inspection inspection = new Inspection();
         inspection.setReceiptItemId(request.getReceiptItemId());

         Users user = userRepository.findById(request.getInspectorId())
               .orElseThrow(() -> new RuntimeException("존재하지 않는 검수자입니다."));
         inspection.setUser(user);

         inspection.setInspectionDate(request.getInspectionDate());
         inspection.setInspectionType(request.getInspectionType());
         inspection.setQuantity(request.getQuantity());
         inspection.setDefectQuantity(request.getDefectQuantity());
         inspection.setInspectionResult(request.getInspectionResult());
         inspection.setNotes(request.getNotes());
         inspection.setCreatedAt(LocalDateTime.now());
         
         inspectionRepository.save(inspection);

         // 입고 데이터 및 창고 코드 조회
         ReceipItem receiptItem = receiptItemRepository.findById(request.getReceiptItemId())
               .orElseThrow(() -> new RuntimeException("입고 상세 내역을 찾을 수 없습니다."));
         Receipt receipt = receiptRepository.findById(receiptItem.getReceiptId())
               .orElseThrow(() -> new RuntimeException("입고 마스터 정보를 찾을 수 없습니다."));
         String warehouseCode = receipt.getWarehouseCode();

         // 재고 조회
         Stock stock = stockRepository.findByProductIdAndWarehouseCode(receiptItem.getProductId(), warehouseCode)
               .orElseThrow(() -> new RuntimeException("해당 품목의 창고 재고가 존재하지 않습니다."));

         Long beforeQty = stock.getQuantity().longValue();
         Long defectQty = request.getDefectQuantity() == null ? 0L : request.getDefectQuantity();
         // Long acceptedQty = request.getQuantity() - defectQty;

         // 검수 단계에서 추가적인 불량이 발견되면 그만큼 재고를 깍는 것이 맞음.
         if(defectQty > 0) {
            if(stock.getQuantity() < defectQty) {
               throw new RuntimeException("추가 불량 수량이 현재 재고보다 많습니다.");
            }

            stock.setQuantity(stock.getQuantity() - defectQty.intValue());
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepository.save(stock);
         }

         // 재고이력 생성 및 저장
         StockHistory history = new StockHistory();
         history.setStockId(stock.getStockId());
         history.setHistoryType("INSPECTION_DEFECT");
         history.setChangeQty(defectQty);
         history.setBeforeQty(beforeQty);
         history.setAfterQty(stock.getQuantity().longValue());
         history.setRelatedReceiptItemId(receiptItem.getReceiptItemId());
         history.setReason("검수 불량 차감(" + request.getNotes() + ")");
         // hisotry.setReason("검수 완료");
         history.setCreatedAt(LocalDateTime.now());
         history.setCreatedBy(request.getUserId());
         
         StockHistoryRepository.save(history);
   }

}

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





