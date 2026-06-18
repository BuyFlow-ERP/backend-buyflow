package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseOrderDto;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.PurchaseOrderRepository;
import com.buyflow.erp.Repository.SupplierRepository;
import com.buyflow.erp.Repository.UserRepository;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository; // 아이템 저장을 위해 필요 시 주입
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	private final SupplierRepository supplierRepository;
	private final UserRepository userRepository;
    private final PurchaseOrderRepository orderRepository;
    // 만약 아이템을 별도로 save 해야 한다면 JpaRepository<PurchaseOrderItem, Long>를 상속받은 레포지토리를 주입받으세요.
    private final PurchaseOrderItemRepository orderItemRepository; 

    @Override
    public PurchaseOrderDto.Response createOrder(PurchaseOrderDto.Request request) {
    	Supplier supplier = supplierRepository.findById(request.getSupplierId())
    			.orElseThrow(() -> new EntityNotFoundException("공급업체가 존재하지 않습니다. ID: " + request.getSupplierId()));
    	
    	Users user = userRepository.findById(request.getCreatedBy())
    			.orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다. ID: " + request.getCreatedBy()));
        
    	// 1. 부모인 발주서 엔티티를 먼저 빌드 (금액은 우선 0원 처리)
        PurchaseOrder order = PurchaseOrder.builder()
                .supplier(supplier)
                .user(user)
                .createdAt(LocalDateTime.now())
                .orderStatus("PENDING")
                .dueDate(request.getDueDate())
                .totalAmount(0.0)
                .build();

//        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();
        
        // 1-2. 화면에서 넘어온 내부 static Item DTO를 꺼내어 엔티티로 변환.
        Double total = 0.0;
        for (PurchaseOrderDto.Item itemReq : request.getItems()) {
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())   // Long 타입
                    .unitPrice(itemReq.getUnitPrice()) // Double 타입
                    .build();
            
            order.addItem(item); // 빈 리스트에 차곡차곡 담기.
            total += itemReq.getUnitPrice() * itemReq.getQuantity();
        } 

        // 1-4. 총 금액 업데이트 후 최종 반영
        order.setTotalAmount(total);
        
        // 리턴값은 새로 정돈한 DTO의 만능 변환기(.from)를 써서 반환
        // 연관관계가 없으므로 엔티티와 저장한 자식 리스트(itemsToSave)를 같이 던져줌.
        // 발주서를 먼저 영속화(DB 저장)하여 고유 번호(ORDER_ID)를 받아옵니다.
        PurchaseOrder savedOrder = orderRepository.save(order);
        
        return PurchaseOrderDto.Response.from(savedOrder);
    }
    
    // 2. 발주 단건 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDto.Response getOrderWithItems(Long orderId) {
    	// N+1
        PurchaseOrder order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));
        
        return PurchaseOrderDto.Response.from(order);
    }

    // 3. 발주 목록 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<PurchaseOrderDto.Response> getOrderList(PurchaseOrderDto.SearchCondition condition) {
        int safePage = Math.max(condition.getPage(), 1);
        int safeSize = Math.max(condition.getSize(), 1);
        Pageable pageable = PageRequest.of(safePage - 1, safeSize);
        
        String orderNo = (condition.getOrderNo() == null || condition.getOrderNo().isEmpty()) ? null : condition.getOrderNo();
        String supplierName = (condition.getSupplierName() == null || condition.getSupplierName().isEmpty() || condition.getSupplierName().equals("전체 공급업체")) ? null : condition.getSupplierName();
        String userName = (condition.getUserName() == null || condition.getUserName().isEmpty()) ? null : condition.getUserName();
        String status = (condition.getOrderStatus() == null || condition.getOrderStatus().isEmpty() || condition.getOrderStatus().equals("전체")) ? null : condition.getOrderStatus();

        Page<PurchaseOrder> orderPage = orderRepository.searchOrdersAdvanced(
                orderNo,
                supplierName,
                userName,
                status,
                pageable
        );

        List<PurchaseOrderDto.Response> dtoList = orderPage.getContent().stream()
                        .map(PurchaseOrderDto.Response::from)
                        .toList();
        
        return new PageResponse<>(
                dtoList,
                new PageResponse.Pagination(
                    orderPage.getNumber() + 1,
                    orderPage.getSize(),
                    orderPage.getTotalElements(),
                    orderPage.getTotalPages()
                )
        );
    }

    // 4. 발주 수정
    @Override
    public PurchaseOrderDto.Response updateOrder(Long orderId, PurchaseOrderDto.Request request) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        if ("APPROVED".equals(order.getOrderStatus())) {
        	throw new RuntimeException("이미 승인 완료된 발주는 수정할 수 없습니다.");
        }
        
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getDueDate() != null) {
            order.setDueDate(request.getDueDate());
        }
        
        if (request.getSupplierId() != null) {
        	Supplier supplier = supplierRepository.findById(request.getSupplierId())
        			.orElseThrow(() -> new EntityNotFoundException("공급업체 없음"));
        	order.setSupplier(supplier);
        }

        // 기존 아이템 전체 삭제
        orderItemRepository.deleteByPurchaseOrder_OrderId(orderId);

        Double total = 0.0;
        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();

        for (PurchaseOrderDto.Item itemReq : request.getItems()) {
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(order)
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            itemsToSave.add(item);
            total += itemReq.getUnitPrice() * itemReq.getQuantity();
        }

        orderItemRepository.saveAll(itemsToSave);
        order.setTotalAmount(total);
        
        PurchaseOrder updatedOrder = orderRepository.save(order);
        
        // 수정 완료 후에도 똑같이 변환기를 거쳐 DTO로 리턴.
        return PurchaseOrderDto.Response.from(updatedOrder);
    }

    //5. 발주 삭제
    @Override
    public void deleteOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        if ("APPROVED".equals(order.getOrderStatus())) {
        	throw new RuntimeException("이미 승인 완료된 발주는 삭제할 수 없습니다.");
        }
        
        // 아이템 먼저 지우고 발주서 삭제
        orderItemRepository.deleteByPurchaseOrder_OrderId(orderId);
        orderRepository.delete(order);
    }
}