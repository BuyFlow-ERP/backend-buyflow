package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PurchaseOrderRequest;
import com.buyflow.erp.Dto.PurchaseOrderResponse;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Repository.PurchaseOrderRepository;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository; // 아이템 저장을 위해 필요 시 주입
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    private final PurchaseOrderRepository orderRepository;
    // 만약 아이템을 별도로 save 해야 한다면 JpaRepository<PurchaseOrderItem, Long>를 상속받은 레포지토리를 주입받으세요.
    private final PurchaseOrderItemRepository orderItemRepository; 

    @Override
    public PurchaseOrder createOrder(PurchaseOrderRequest request) {
        // 1. 부모인 발주서 엔티티를 먼저 빌드 (금액은 우선 0원 처리)
        PurchaseOrder order = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .orderStatus("PENDING")
                .dueDate(request.getDueDate())
                .totalAmount(BigDecimal.ZERO)
                .build();

        // 발주서를 먼저 영속화(DB 저장)하여 고유 번호(ORDER_ID)를 받아옵니다.
        PurchaseOrder savedOrder = orderRepository.save(order);

        Double total = 0.0;
        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();

        // 2. 다른 분이 만든 PurchaseOrderItem 형식에 맞춰 데이터 생성
        for (PurchaseOrderItemDto itemReq : request.getItems()) {
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .orderId(savedOrder.getOrderId()) // 영속화된 발주서 ID를 매핑!
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())   // Long 타입
                    .unitPrice(itemReq.getUnitPrice()) // Double 타입
                    .build();

            itemsToSave.add(item);

            // Double 타입 금액 합산 계산
            total += itemReq.getUnitPrice() * itemReq.getQuantity();
        }

        // 3. 생성된 아이템 리스트 일괄 저장
        orderItemRepository.saveAll(itemsToSave);

        // 4. 총 금액 업데이트 후 최종 반영
        savedOrder.setTotalAmount(BigDecimal.valueOf(total));
        return orderRepository.save(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getOrderWithItems(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));
        
        // 연관관계가 없으므로 아이템 테이블에서 orderId로 직접 조회해 와야 합니다.
        List<PurchaseOrderItem> items = orderItemRepository.findByOrderId(orderId);
        
        // 조회된 아이템들을 Response DTO 형식으로 변환
        List<PurchaseOrderItemDto> itemDtos = items.stream()
                .map(item -> PurchaseOrderItemDto.builder()
                        .orderItemId(item.getOrderItemId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();

        return PurchaseOrderResponse.builder()
                .orderId(order.getOrderId())
                .supplierId(order.getSupplierId())
                .createdBy(order.getCreatedBy())
                .createdAt(order.getCreatedAt())
                .orderStatus(order.getOrderStatus())
                .dueDate(order.getDueDate())
                .totalAmount(order.getTotalAmount())
                .items(itemDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getOrderList() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
                    List<PurchaseOrderItemDto> itemDtos = items.stream()
                            .map(item -> PurchaseOrderItemDto.builder()
                                    .orderItemId(item.getOrderItemId())
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .unitPrice(item.getUnitPrice())
                                    .build())
                            .toList();
                            
                    return PurchaseOrderResponse.builder()
                            .orderId(order.getOrderId())
                            .supplierId(order.getSupplierId())
                            .orderStatus(order.getOrderStatus())
                            .totalAmount(order.getTotalAmount())
                            .items(itemDtos)
                            .build();
                })
                .toList();
    }

    @Override
    public PurchaseOrder updateOrder(Long orderId, PurchaseOrderRequest request) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getDueDate() != null) {
            order.setDueDate(request.getDueDate());
        }

        // 기존 아이템 삭제 로직 (연관관계가 없으므로 해당 orderId인 아이템을 직접 지움)
        orderItemRepository.deleteByOrderId(orderId);

        Double total = 0.0;
        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();

        for (PurchaseOrderItemDto itemReq : request.getItems()) {
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .orderId(order.getOrderId())
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            itemsToSave.add(item);
            total += itemReq.getUnitPrice() * itemReq.getQuantity();
        }

        orderItemRepository.saveAll(itemsToSave);
        order.setTotalAmount(BigDecimal.valueOf(total));
        
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        // 아이템 먼저 지우고 발주서 삭제
        orderItemRepository.deleteByOrderId(orderId);
        orderRepository.delete(order);
    }
}