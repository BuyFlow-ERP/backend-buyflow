package com.buyflow.erp.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

private final PurchaseOrderRepository orderRepository;
    private final ProductRepository productRepository; // 품목 검증용 (필요시)

    @Override
    public PurchaseOrder createOrder(PurchaseOrderRequest request) {
        PurchaseOrder order = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .orderStatus("PENDING")
                .dueDate(request.getDueDate())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            // 품목 검증 (필요하면 주석 해제)
            // Product product = productRepository.findById(itemReq.getProductId())
            //         .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다. ID: " + itemReq.getProductId()));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            order.addItem(item);

            total = total.add(
                    itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getOrderWithItems(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getOrderList() {
        return orderRepository.findAll().stream()
                .map(PurchaseOrderResponse::from)
                .toList();
    }

    @Override
    public PurchaseOrder updateOrder(Long orderId, PurchaseOrderRequest request) {
        PurchaseOrder order = getOrderWithItems(orderId);

        // 기본 정보 업데이트
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getDueDate() != null) {
            order.setDueDate(request.getDueDate());
        }

        // 아이템 전체 교체
        order.getItems().clear();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            order.addItem(item);

            total = total.add(
                    itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID: " + orderId));

        orderRepository.delete(order);
    }
}
