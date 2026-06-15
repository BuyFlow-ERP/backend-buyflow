package com.buyflow.erp.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.InspectionRepository;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import com.buyflow.erp.Repository.ReceiptRepository;
import com.buyflow.erp.Repository.StockHistoryRepository;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
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
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<InspectionDto.ListResponse> getInspections(InspectionDto.SearchCondition condition) {
      int safePage = Math.max(condition.getPage(), 0);
      int safeSize = Math.max(condition.getSize(), 1);
      Pageable pageable = PageRequest.of(safePage, safeSize);

      String result = (condition.getInspectionResult() == null || condition.getInspectionResult().isEmpty() || condition.getInspectionResult().equals("전체")) ? null : condition.getInspectionResult();

      Page<Inspection> inspectionPage = inspectionRepository.searchInspections(
            condition.getReceiptItemId(),
            result,
            pageable
      );

      List<InspectionDto.ListResponse> dtoList = inspectionPage.getContent().stream()
                  .map(inspection -> new InspectionDto.ListResponse(
                        inspection.getInspectionId(),
                        inspection.getInspectionDate(),
                        inspection.getInspectionType(),
                        inspection.getInspectionResult(),
                        inspection.getReceiptItemId(),
                        inspection.getUser() != null ? inspection.getUser().getUserId() : null,
                        inspection.getQuantity(),
                        inspection.getDefectQuantity()
                  ))
                  .collect(Collectors.toList());

        return new PageResponse<>(
                  dtoList,
                  new PageResponse.Pagination(
                        inspectionPage.getNumber() + 1,
                        inspectionPage.getSize(),
                        inspectionPage.getTotalElements(),
                        inspectionPage.getTotalPages()
                  )
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Inspection getInspection(Long inspectionId) {
    	return inspectionRepository.findById(inspectionId)
    			.orElseThrow(() -> new EntityNotFoundException("해당 검수 내역을 찾을 수 없습니다. ID: "+ inspectionId));
    }
    
    @Override
    public void saveInspection(InspectionDto.CreateRequest request) {
         
         // 중복 검수 예외 처리
         if(inspectionRepository.existsByReceiptItemId(request.getReceiptItemId())) {
            throw new RuntimeException("이미 검수 완료된 입고입니다.");
         }
         
         //defectQuantity 혹은 defectQty중 어떤 것을 보내든 null이 아닌 값을 안전하게 주기
         Long finalDefectQty = request.getDefectQuantity() != null ? request.getDefectQuantity() : 
        	 					(request.getDefectQty() != null ? request.getDefectQty() : 0L);
         
         // 검수 정보 빌드 및 저장
         Inspection inspection = new Inspection();
         inspection.setReceiptItemId(request.getReceiptItemId());

         Users user = userRepository.findById(request.getInspectorId())
               .orElseThrow(() -> new RuntimeException("존재하지 않는 검수자입니다."));
         inspection.setUser(user);

         inspection.setInspectionDate(request.getInspectionDate());
         inspection.setInspectionType(request.getInspectionType());
         inspection.setQuantity(request.getQuantity());
         
         inspection.setDefectQuantity(finalDefectQty);
         
         inspection.setInspectionResult(request.getInspectionResult());
         inspection.setNotes(request.getNotes());
         inspection.setCreatedAt(LocalDateTime.now());
         
         inspectionRepository.save(inspection);

         // 입고 데이터 및 창고 코드 조회
         ReceiptItem receiptItem = receiptItemRepository.findById(request.getReceiptItemId())
               .orElseThrow(() -> new RuntimeException("입고 상세 내역을 찾을 수 없습니다."));
         Receipt receipt = receiptRepository.findById(receiptItem.getReceiptId())
               .orElseThrow(() -> new RuntimeException("입고 마스터 정보를 찾을 수 없습니다."));
         String warehouseCode = receipt.getWarehouseCode();

         // 재고 조회
         Stock stock = stockRepository.findByProductIdAndWarehouseCode(receiptItem.getProductId(), warehouseCode)
               .orElseThrow(() -> new RuntimeException("해당 품목의 창고 재고가 존재하지 않습니다."));

         Long beforeQty = stock.getQuantity().longValue();
//         Long defectQty = request.getDefectQuantity() == null ? 0L : request.getDefectQuantity();
         // Long acceptedQty = request.getQuantity() - defectQty;

         // 검수 단계에서 추가적인 불량이 발견되면 그만큼 재고를 깍는 것이 맞음.
         if(finalDefectQty > 0) {
            if(stock.getQuantity() < finalDefectQty) {
               throw new RuntimeException("추가 불량 수량이 현재 재고보다 많습니다.");
            }

            stock.setQuantity(stock.getQuantity() - finalDefectQty.intValue());
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepository.save(stock);
         }

         // 재고이력 생성 및 저장
         StockHistory history = new StockHistory();
         history.setStockId(stock.getStockId());
         history.setHistoryType("INSPECTION_DEFECT");
         history.setChangeQty(finalDefectQty);
         history.setBeforeQty(beforeQty);
         history.setAfterQty(stock.getQuantity().longValue());
         history.setRelatedReceiptItemId(receiptItem.getReceiptItemId());
         history.setReason("검수 불량 차감(" + request.getNotes() + ")");
         // hisotry.setReason("검수 완료");
         history.setCreatedAt(LocalDateTime.now());
         history.setCreatedBy(user.getUserName());
         
         stockHistoryRepository.save(history);
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





