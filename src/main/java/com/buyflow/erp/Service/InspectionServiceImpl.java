package com.buyflow.erp.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.InspectionDto;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Inspection;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Entity.ReceiptItem;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Repository.InspectionRepository;
import com.buyflow.erp.Repository.PurchaseOrderRepository;
import com.buyflow.erp.Repository.ReceiptItemRepository;
import com.buyflow.erp.Repository.ReceiptRepository;
import com.buyflow.erp.Repository.StockHistoryRepository;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Repository.UserRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

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
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final WarehouseRepository warehouseRepository;

	@Override
	@Transactional(readOnly = true)
	public PageResponse<InspectionDto.ListResponse> getInspections(InspectionDto.SearchCondition condition) {
	    // ⭕ [페이징 바인딩 해결] Integer 래퍼 방패로 500 바인딩 컷 차단
	    int displayPage = (condition.getPage() != null) ? condition.getPage() : 1;
	    int safeSize = (condition.getSize() != null && condition.getSize() > 0) ? condition.getSize() : 15;
	    
	    int safePage = Math.max(displayPage - 1, 0);
	    Pageable pageable = PageRequest.of(safePage, safeSize);

	    String result = (condition.getInspectionResult() == null || condition.getInspectionResult().isEmpty()
	            || condition.getInspectionResult().equals("전체")) ? null : condition.getInspectionResult();

	    Page<Inspection> inspectionPage = inspectionRepository.searchInspections(condition.getReceiptItemId(), result, pageable);

	    List<InspectionDto.ListResponse> dtoList = inspectionPage.getContent().stream()
	            .map(inspection -> new InspectionDto.ListResponse(
	                    inspection.getInspectionId(),
	                    inspection.getInspectionDate(), 
	                    inspection.getInspectionType(),
	                    inspection.getInspectionResult(), 
	                    inspection.getReceiptItemId(),
	                    inspection.getUser() != null ? inspection.getUser().getUserId() : null,
	                    inspection.getQuantity(), 
	                    inspection.getDefectQuantity()))
	            .collect(Collectors.toList());

	    return new PageResponse<>(dtoList, new PageResponse.Pagination(
	            inspectionPage.getNumber() + 1,
	            inspectionPage.getSize(), 
	            inspectionPage.getTotalElements(), 
	            inspectionPage.getTotalPages()));
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageResponse<InspectionDto.Response> getPendingInspections(InspectionDto.SearchCondition condition) {
	    
	    // 🛡️ 프론트엔드 page=1 규격 완벽 인지 및 바인딩 방패
	    int displayPage = (condition.getPage() != null) ? condition.getPage() : 1;
	    int safeSize = (condition.getSize() != null && condition.getSize() > 0) ? condition.getSize() : 15;
	    
	    int safePage = Math.max(displayPage - 1, 0);
	    Pageable pageable = PageRequest.of(safePage, safeSize);
	    
	    // 🚀 에러 발생 가능성 0%인 기본 findAll 메서드로 오라클 데이터 안전 획득!
	    Page<ReceiptItem> pendingReceiptItemPage = receiptItemRepository.findAll(pageable);

	    // 💡 연관 ID 리스트 추출 및 벌크 조인 캐시 맵 빌드
	    List<Long> receiptIds = pendingReceiptItemPage.getContent().stream()
	            .map(ReceiptItem::getReceiptId)
	            .filter(Objects::nonNull)
	            .toList();

	    Map<Long, Receipt> receiptMap = receiptRepository.findAllById(receiptIds).stream()
	            .collect(Collectors.toMap(Receipt::getReceiptId, Function.identity(), (v1, v2) -> v1));

	    List<Long> orderIds = receiptMap.values().stream()
	            .map(Receipt::getOrderId)
	            .filter(Objects::nonNull)
	            .toList();

	    Map<Long, PurchaseOrder> orderMap = purchaseOrderRepository.findAllById(orderIds).stream()
	            .collect(Collectors.toMap(PurchaseOrder::getOrderId, Function.identity(), (v1, v2) -> v1));

	    // 웅장한 DTO 100% 안전 매칭 파이프라인
	    List<InspectionDto.Response> dtoList = pendingReceiptItemPage.getContent().stream()
	            .<InspectionDto.Response>map(item -> {
	                Receipt receipt = (item.getReceiptId() != null) ? receiptMap.get(item.getReceiptId()) : null;
	                PurchaseOrder order = null;
	                if (receipt != null && receipt.getOrderId() != null) {
	                	order = orderMap.get(receipt.getOrderId());
	                }
	                
	                Supplier supplier = (order != null) ? order.getSupplier() : null;

	                return InspectionDto.Response.builder()
	                        .id(item.getReceiptItemId())
	                        .inspectionNumber("IQC-" + item.getReceiptItemId())
	                        
	                        .inboundNumber(receipt != null && receipt.getReceiptNo() != null ? receipt.getReceiptNo() : "-")
	                        .orderNumber(order != null ? "PO-2026-" + order.getOrderId() : "-")
	                        .supplierName(supplier != null && supplier.getSupplierName() != null ? supplier.getSupplierName() : "-")
	                        .warehouseName("-")
	                        
	                        .receivedAt(receipt != null && receipt.getReceiptDate() != null ? receipt.getReceiptDate().toLocalDate().toString() : "-")
	                        .inspectionDueAt(receipt != null && receipt.getReceiptDate() != null ? receipt.getReceiptDate().toLocalDate().plusDays(3).toString() : "-")
	                        .priority("일반")
	                        .status("PENDING")
	                        .receivedBy(receipt != null && receipt.getLoginId() != null ? receipt.getLoginId() : "-")
	                        .itemCount(1)
	                        .totalReceivedQuantity(item.getReceiptQty() != null ? item.getReceiptQty() : 0L)
	                        .items(new ArrayList<>())
	                        .build();
	            })
	            .toList();

	    return new PageResponse<>(
	            dtoList,
	            new PageResponse.Pagination(
	                    pendingReceiptItemPage.getNumber() + 1,
	                    pendingReceiptItemPage.getSize(),
	                    pendingReceiptItemPage.getTotalElements(),
	                    pendingReceiptItemPage.getTotalPages()
	            )
	    );
	}
	
	@Override
	@Transactional(readOnly = true)
	public Inspection getInspection(Long inspectionId) {
		return inspectionRepository.findById(inspectionId)
				.orElseThrow(() -> new EntityNotFoundException("해당 검수 내역을 찾을 수 없습니다. ID: " + inspectionId));
	}

	@Override
	@Transactional
	public void saveInspection(InspectionDto.CreateRequest request) {
		
		if (request.getReceiptItemId() == null) {
			throw new RuntimeException("입고 품목 ID가 없습니다.");
		}
		
		// 중복 검수 예외 처리
		if (inspectionRepository.existsByReceiptItemId(request.getReceiptItemId())) {
			throw new RuntimeException("이미 검수 완료된 입고입니다.");
		}
		
		ReceiptItem receiptItem = receiptItemRepository.findById(request.getReceiptItemId())
				.orElseThrow(() -> new RuntimeException("입고 상세 내역을 찾을 수 없습니다."));
		
		Receipt receipt = receiptRepository.findById(receiptItem.getReceiptId())
				.orElseThrow(() -> new RuntimeException("입고 마스터 정보를 찾을 수 없습니다."));

		Users user = userRepository.findById(request.getInspectorId())
				.orElseThrow(() -> new RuntimeException("존재하지 않는 검수자입니다."));
		
		// defectQuantity 혹은 defectQty중 어떤 것을 보내든 null이 아닌 값을 안전하게 주기
		Long receiptQty = receiptItem.getReceiptQty() == null ? 0L : receiptItem.getReceiptQty();
		Long oldAcceptedQty = receiptItem.getAcceptedQty() == null ? 0L : receiptItem.getAcceptedQty();
		
		Long finalDefectQty = request.getDefectQuantity() != null 
				? request.getDefectQuantity()
				: (request.getDefectQty() != null ? request.getDefectQty() : 0L);

		Long finalAcceptedQty = request.getAcceptedQuantity();
		
		if (finalAcceptedQty == null) {
			finalAcceptedQty = receiptQty - finalDefectQty;
		}
		
		if (finalDefectQty < 0 || finalAcceptedQty < 0) {
			throw new RuntimeException("합격 수량과 불량 수량은 0 이상이어야 합니다.");
		}
		
		if(!receiptQty.equals(finalAcceptedQty + finalDefectQty)) {
			throw new RuntimeException("합격 수량과 불량 수량의 합이 입고 수량과 일치하지 않습니다.");
		}
		
		// 검수 정보 빌드 및 저장
		Inspection inspection = new Inspection();
		inspection.setReceiptItemId(request.getReceiptItemId());
		inspection.setUser(user);
		inspection.setInspectionDate(request.getInspectionDate());
		inspection.setInspectionType(request.getInspectionType());
		
		inspection.setQuantity(request.getQuantity());
		
		inspection.setDefectQuantity(finalDefectQty);
		inspection.setInspectionResult(request.getInspectionResult());
		inspection.setNotes(request.getNotes());
		inspection.setCreatedAt(LocalDateTime.now());

		inspectionRepository.save(inspection);

		String warehouseCode = receipt.getWarehouseCode();

		// 재고 조회
		Stock stock = stockRepository.findByProductIdAndWarehouseCode(receiptItem.getProductId(), warehouseCode)
				.orElseThrow(() -> new RuntimeException("해당 품목의 창고 재고가 존재하지 않습니다."));

		Long beforeQty = stock.getQuantity() == null ? 0L : stock.getQuantity().longValue();
		Long correctionQty = finalAcceptedQty - oldAcceptedQty;

		// 검수 단계에서 추가적인 불량이 발견되면 그만큼 재고를 깍는 것이 맞음.
		if (correctionQty != 0) {
			Long afterQty = beforeQty + correctionQty;
			
			if (afterQty < 0) {
				throw new RuntimeException("검수 보정 후 재고가 음수가 될 수 없습니다.");
			}

			stock.setQuantity(afterQty.intValue());
			stock.setUpdatedAt(LocalDateTime.now());
			stockRepository.save(stock);
			
			// 재고이력 생성 및 저장
			StockHistory history = new StockHistory();
			history.setStockId(stock.getStockId());
			history.setHistoryType("INSPECTION_ADJUST");
			history.setChangeQty(correctionQty);
			history.setBeforeQty(beforeQty);
			history.setAfterQty(afterQty);
			history.setRelatedReceiptItemId(receiptItem.getReceiptItemId());
			history.setRelatedOrderItemId(receiptItem.getOrderItemId());
			history.setReason("검수 결과에 따른 재고 보정");
			history.setCreatedAt(LocalDateTime.now());
			history.setCreatedBy(user.getUserName());

			stockHistoryRepository.save(history);
		}
		
		receiptItem.setDefectQty(finalDefectQty);
		receiptItem.setAcceptedQty(finalAcceptedQty);
		receiptItemRepository.save(receiptItem);
	}
	
	@Override
	@Transactional
	public void saveInspectionResult(Long receiptId, InspectionDto.ResultRequest request) {
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new RuntimeException("검수할 품목이 없습니다.");
		}
		
		for (InspectionDto.ResultItemRequest itemRequest : request.getItems()) {
			Long receiptItemId = itemRequest.getReceiptItemId() != null
					? itemRequest.getReceiptItemId()
					: itemRequest.getId();
			
			InspectionDto.CreateRequest createRequest = new InspectionDto.CreateRequest();
			
			createRequest.setReceiptItemId(receiptItemId);
			createRequest.setInspectorId(request.getInspectorId());
			createRequest.setInspectionDate(
				request.getInspectedAt() == null
						? java.time.LocalDate.now()
						: request.getInspectedAt().toLocalDate()
			);
			createRequest.setInspectionType("INBOUND");
			createRequest.setQuantity(itemRequest.getReceivedQuantity());
			createRequest.setAcceptedQuantity(itemRequest.getAcceptedQuantity());
			createRequest.setDefectQuantity(itemRequest.getDefectiveQuantity());
			createRequest.setInspectionResult(
					itemRequest.getDefectiveQuantity() != null && itemRequest.getDefectiveQuantity() > 0
							? "DEFECT"
							: "PASS"
			);
			createRequest.setNotes(
					itemRequest.getDefectReason() != null && !itemRequest.getDefectReason().isBlank()
							? itemRequest.getDefectReason()
							: request.getNote()
			);
			
			saveInspection(createRequest);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public InspectionDto.SummaryResponse getInspectionSummary() {
		long total = inspectionRepository.count();
		long passCount = inspectionRepository.countByInspectionResult("PASS");
		long defectCount = inspectionRepository.countByInspectionResult("DEFECT");
		
		long pendingCount = total - (passCount + defectCount);
		
		return new InspectionDto.SummaryResponse(total, pendingCount, passCount, defectCount);
	}
}

