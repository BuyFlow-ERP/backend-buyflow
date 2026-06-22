package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.PurchaseOrderDto;
import com.buyflow.erp.Dto.PurchaseOrderItemDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.PurchaseOrderItem;
import com.buyflow.erp.Entity.PurchaseRequest;
import com.buyflow.erp.Entity.PurchaseRequestItem;
import com.buyflow.erp.Entity.Supplier;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.PurchaseOrderRepository;
import com.buyflow.erp.Repository.PurchaseRequestItemRepository;
import com.buyflow.erp.Repository.PurchaseRequestRepository;
import com.buyflow.erp.Repository.SupplierRepository;
import com.buyflow.erp.Repository.UserRepository;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.PurchaseOrderItemRepository; 
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderRepository orderRepository;
    private final PurchaseOrderItemRepository orderItemRepository; 
    private final ProductRepository productRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;

    @Override
    @Transactional
    public PurchaseOrderDto.Response createOrder(PurchaseOrderDto.Request request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 데이터가 비어있습니다.");
        }
        
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("공급업체가 존재하지 않습니다. ID: " + request.getSupplierId()));
        
        Long userIdToFind = request.getCreatedBy();
        if (userIdToFind == null || userIdToFind <= 0) {
            userIdToFind = 5L; 
        }
        
        final Long finalUserId = userIdToFind;
        
        Users user = userRepository.findById(userIdToFind)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다. ID: " + finalUserId));
        
        PurchaseRequest purchaseRequest = null;
        if (request.getRequestId() != null) {
            purchaseRequest = purchaseRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("구매 요청을 찾을 수 없습니다. ID: " + request.getRequestId()));
        }
        
        LocalDateTime finalDueDate = request.getDueDate();
        if (finalDueDate == null && request.getExpectedInboundTo() != null && !request.getExpectedInboundTo().isEmpty()) {
            try {
                finalDueDate = LocalDateTime.parse(request.getExpectedInboundTo() + "T23:59:59");
            } catch (Exception e) {
                finalDueDate = LocalDateTime.now().plusDays(7);
            }
        } else if (finalDueDate == null) {
            finalDueDate = LocalDateTime.now().plusDays(7);
        }
        
        String finalOrderNo = request.getOrderNo();
        if (finalOrderNo == null || finalOrderNo.trim().isEmpty()) {
            String todayPrefix = "PO-" + java.time.LocalDate.now().toString() + "-";
            String maxOrderNo = orderRepository.findMaxOrderNoByToday(todayPrefix + "%"); 

            if (maxOrderNo == null || maxOrderNo.isEmpty()) {
                finalOrderNo = todayPrefix + "0001";
            } else {
                try {
                    String lastPart = maxOrderNo.substring(maxOrderNo.lastIndexOf("-") + 1);
                    String numericPart = lastPart.replaceAll("[^0-9]", "");
                    int nextSeq = numericPart.isEmpty() ? 1 : Integer.parseInt(numericPart) + 1;
                    finalOrderNo = String.format("PO-%s-%04d", java.time.LocalDate.now().toString(), nextSeq);
                } catch (Exception e) {
                    finalOrderNo = todayPrefix + "0001";
                }
            }
        }

        // 🟢 [컴파일 에러 해결 Guard]: 엔티티 빌더 내부에서 에러가 나던 필드들을 걷어내어 컴파일을 정상화합니다.
        PurchaseOrder order = PurchaseOrder.builder()
                .orderNo(finalOrderNo.trim())
                .supplier(supplier)
                .user(user)
                .purchaseRequest(purchaseRequest)
                .createdAt(LocalDateTime.now()) 
                .orderStatus(request.getOrderStatus() != null ? request.getOrderStatus() : "PENDING")
                .dueDate(finalDueDate)
                .totalAmount(0.0) 
                .build();
        
        long totalSupplyAmount = 0L;
        long totalVatAmount = 0L;
        
        for (PurchaseOrderDto.Item itemReq : request.getItems()) {
        	Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + itemReq.getProductId()));
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())   
                    .unitPrice(itemReq.getUnitPrice()) 
                    .build();
            
            order.addItem(item); 
            
            long lineSupply = (long) (itemReq.getUnitPrice() * itemReq.getQuantity());
            long lineVat = (long) Math.floor(lineSupply * 0.1);
            
            totalSupplyAmount += lineSupply;
            totalVatAmount += lineVat;
        } 

        double finalTotalAmount = (double) (totalSupplyAmount + totalVatAmount);
        order.setTotalAmount(finalTotalAmount);
        
        PurchaseOrder savedOrder = orderRepository.save(order);
        
        PurchaseOrder refreshedOrder = orderRepository.findByIdWithItems(savedOrder.getOrderId())
                .orElse(savedOrder);
        
        System.out.println("=== Create 후 refreshed Debug ===");
        System.out.println("PurchaseRequest exists: " + (refreshedOrder.getPurchaseRequest() != null));
        
        return PurchaseOrderDto.Response.from(refreshedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDto.Response getOrderWithItems(Long orderId) {
        PurchaseOrder order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        // 디버깅용 로그 추가
        System.out.println("=== getOrderWithItems Debug ===");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("PurchaseRequest exists: " + (order.getPurchaseRequest() != null));
        
        if (order.getItems() != null) {
            Hibernate.initialize(order.getItems());
        }
        if (order.getPurchaseRequest() != null) {
            Hibernate.initialize(order.getPurchaseRequest());
        }
        
        if (order.getPurchaseRequest() != null) {
            System.out.println("RequestNo: " + order.getPurchaseRequest().getRequestNo());
            System.out.println("Title: " + order.getPurchaseRequest().getTitle());
        }

        if (order.getPurchaseRequest() != null) {
            Hibernate.initialize(order.getPurchaseRequest());
        }

        return PurchaseOrderDto.Response.from(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto.ItemResponse> getApprovedRequestItems(Long requestId) {        
        List<PurchaseRequestItem> items = purchaseRequestItemRepository.findByRequestIdOrderByRequestItemIdAsc(requestId);
        
        Map<Long, Product> productMap = productRepository.findAllById(
                        items.stream()
                                .map(PurchaseRequestItem::getProductId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return items.stream()
                .map(item -> {
                    Product product = productMap.get(item.getProductId());
                    
                    Long defaultPrice = 0L;
                    if (item.getEstimatedUnitPrice() != null && item.getEstimatedUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
                        defaultPrice = item.getEstimatedUnitPrice().longValue();
                    } else if (product != null && product.getUnitPrice() != null) {
                        defaultPrice = product.getUnitPrice();
                    }
                    
                    return PurchaseOrderDto.ItemResponse.builder()
                            .requestItemId(item.getRequestItemId()) 
                            .itemCode(product != null ? product.getProductNo() : "") 
                            .itemName(product != null ? product.getProductName() : "") 
                            .specification(product != null ? product.getSpec() : "") 
                            .requestedQuantity(item.getRequestQuantity()) 
                            .orderQuantity(item.getRequestQuantity())     
                            .unit(product != null ? product.getUnit() : "") 
                            .unitPrice(defaultPrice) 
                            .build();
                })
                .toList();
    }
 
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

        // 🟢 기존의 커스텀 레포지토리 쿼리를 안전하게 호출
        Page<PurchaseOrder> orderPage = orderRepository.searchOrdersAdvanced(
                orderNo,
                supplierName,
                userName,
                status,
                pageable
        );

        // 🎯 [정밀 디버깅 선로]: DB에서 날것으로 뽑아온 찐 데이터 상태를 이클립스 콘솔에 출력합니다.
        System.out.println("====== 🔥 [목록 쿼리 최종 추적 현장산] ======");
        if (orderPage != null && orderPage.getContent() != null) {
            for (PurchaseOrder po : orderPage.getContent()) {
                System.out.println(String.format(
                    "📌 발주번호: %s | DB에서 읽어온 금액(TotalAmount): %s | DB에서 읽어온 날짜(CreatedAt): %s | 품목개수: %d",
                    po.getOrderNo(),
                    po.getTotalAmount(),
                    po.getCreatedAt(),
                    po.getItems() != null ? po.getItems().size() : 0
                ));
            }
        }
        System.out.println("=========================================");

        List<PurchaseOrderDto.Response> dtoList = orderPage.getContent().stream()
                        .map(PurchaseOrderDto.Response::from)
                        .toList();
        
        // 🚀 [타입 추론 에러 진압]: 제네릭과 페이징 객체를 명확히 선언하여 빨간 줄을 완벽하게 차단합니다.
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
    
    @Override
    @Transactional
    public PurchaseOrderDto.Response updateOrder(Long orderId, PurchaseOrderDto.Request request) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        if ("APPROVED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new RuntimeException("이미 승인 완료되거나 취소된 발주는 수정할 수 없습니다.");
        }

        // 기본 정보 업데이트
        if (request.getRequestId() != null) {
            PurchaseRequest purchaseRequest = purchaseRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("구매 요청을 찾을 수 없습니다. ID: " + request.getRequestId()));
            order.setPurchaseRequest(purchaseRequest);
        }
        
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getDueDate() != null) {
            order.setDueDate(request.getDueDate());
        }
        if (request.getMemo() != null) {
            order.setMemo(request.getMemo());
        }

        // 공급업체 변경
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("공급업체가 존재하지 않습니다. ID: " + request.getSupplierId()));
            order.setSupplier(supplier);
        }

        // 기존 아이템 전체 삭제 후 새로 생성
        orderItemRepository.deleteByPurchaseOrder_OrderId(orderId);

        long totalSupplyAmount = 0L;
        long totalVatAmount = 0L;
        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();

        for (PurchaseOrderDto.Item itemReq : request.getItems()) {
        	Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + itemReq.getProductId()));
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            itemsToSave.add(item);
            order.addItem(item);  // 양방향 관계 설정

            long lineSupply = (long) (itemReq.getUnitPrice() * itemReq.getQuantity());
            long lineVat = (long) Math.floor(lineSupply * 0.1);

            totalSupplyAmount += lineSupply;
            totalVatAmount += lineVat;
        }

        orderItemRepository.saveAll(itemsToSave);

        double finalTotalAmount = (double) (totalSupplyAmount + totalVatAmount);
        order.setTotalAmount(finalTotalAmount);

        // 업데이트 후 저장
        PurchaseOrder updatedOrder = orderRepository.save(order);

        // 🚀 refreshedOrder: LAZY 관계까지 모두 로딩된 상태로 DTO 변환
        PurchaseOrder refreshedOrder = orderRepository.findByIdWithItems(updatedOrder.getOrderId())
                .orElse(updatedOrder);

        System.out.println("=== Create 후 refreshed Debug ===");
        System.out.println("PurchaseRequest exists: " + (refreshedOrder.getPurchaseRequest() != null));
        
        return PurchaseOrderDto.Response.from(refreshedOrder);
    }

//    @Override
//    public PurchaseOrderDto.Response updateOrder(Long orderId, PurchaseOrderDto.Request request) {
//        PurchaseOrder order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));
//
//        if ("APPROVED".equals(order.getOrderStatus())) {
//            throw new RuntimeException("이미 승인 완료된 발주는 수정할 수 없습니다.");
//        }
//        
//        if (request.getOrderStatus() != null) {
//            order.setOrderStatus(request.getOrderStatus());
//        }
//        if (request.getDueDate() != null) {
//            order.setDueDate(request.getDueDate());
//        }
//        
//        // 🟢 [지뢰 해제 3]: 수정 시에도 원본 날짜가 유지되도록 처리하며, 존재하지 않는 필드는 셋하지 않아 빨간 줄을 완벽 방어합니다.
//        if (order.getCreatedAt() == null) {
//            order.setCreatedAt(LocalDateTime.now());
//        }
//        
//        if (request.getSupplierId() != null) {
//            Supplier supplier = supplierRepository.findById(request.getSupplierId())
//                    .orElseThrow(() -> new EntityNotFoundException("공급업체 없음"));
//            order.setSupplier(supplier);
//        }
//
//        // 기존 아이템 전체 삭제
//        orderItemRepository.deleteByPurchaseOrder_OrderId(orderId);
//
//        long totalSupplyAmount = 0L;
//        long totalVatAmount = 0L;
//        List<PurchaseOrderItem> itemsToSave = new ArrayList<>();
//
//        for (PurchaseOrderDto.Item itemReq : request.getItems()) {
//            PurchaseOrderItem item = PurchaseOrderItem.builder()
//                    .purchaseOrder(order)
//                    .productId(itemReq.getProductId())
//                    .quantity(itemReq.getQuantity())
//                    .unitPrice(itemReq.getUnitPrice())
//                    .build();
//
//            itemsToSave.add(item);
//
//            long lineSupply = (long) (itemReq.getUnitPrice() * itemReq.getQuantity());
//            long lineVat = (long) Math.floor(lineSupply * 0.1);
//            
//            totalSupplyAmount += lineSupply;
//            totalVatAmount += lineVat;
//        }
//
//        orderItemRepository.saveAll(itemsToSave);
//        
//        double finalTotalAmount = (double) (totalSupplyAmount + totalVatAmount);
//        order.setTotalAmount(finalTotalAmount);
//        
//        PurchaseOrder updatedOrder = orderRepository.save(order);
//        return PurchaseOrderDto.Response.from(updatedOrder);
//    }

    @Override
    public void deleteOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        if ("APPROVED".equals(order.getOrderStatus())) {
            throw new RuntimeException("이미 승인 완료된 발주는 삭제할 수 없습니다.");
        }
        
        orderItemRepository.deleteByPurchaseOrder_OrderId(orderId);
        orderRepository.delete(order);
    }
}