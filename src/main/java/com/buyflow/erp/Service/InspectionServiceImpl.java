package com.buyflow.erp.Service;

import java.time.LocalDateTime;
import java.util.List;
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

		String result = (condition.getInspectionResult() == null || condition.getInspectionResult().isEmpty()
				|| condition.getInspectionResult().equals("전체")) ? null : condition.getInspectionResult();

		Page<Inspection> inspectionPage = inspectionRepository.searchInspections(condition.getReceiptItemId(), result,
				pageable);

		List<InspectionDto.ListResponse> dtoList = inspectionPage.getContent().stream()
				.map(inspection -> new InspectionDto.ListResponse(inspection.getInspectionId(),
						inspection.getInspectionDate(), inspection.getInspectionType(),
						inspection.getInspectionResult(), inspection.getReceiptItemId(),
						inspection.getUser() != null ? inspection.getUser().getUserId() : null,
						inspection.getQuantity(), inspection.getDefectQuantity()))
				.collect(Collectors.toList());

		return new PageResponse<>(dtoList, new PageResponse.Pagination(inspectionPage.getNumber() + 1,
				inspectionPage.getSize(), inspectionPage.getTotalElements(), inspectionPage.getTotalPages()));
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

