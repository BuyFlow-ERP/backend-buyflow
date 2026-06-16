package com.buyflow.erp.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Dto.StockDto;
import com.buyflow.erp.Entity.Stock;
import com.buyflow.erp.Repository.StockRepository;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Entity.Warehouse;
import com.buyflow.erp.Repository.ProductRepository;
import com.buyflow.erp.Repository.StockHistoryRepository;
import com.buyflow.erp.Repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 최적화
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<StockDto.Response> findAllStocks() {
        // DB에서 재고 엔티티 전체 리스트 조회
        List<Stock> stocks = stockRepository.findAll();

        // 엔티티 리스트를 화면용 Response DTO List로 변환
        return stocks.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDto.Response> findStocksByProductId(
            Long productId) {

        List<Stock> stocks = stockRepository.findByProductId(
                productId);

        return stocks.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDto.Response> findStocksByWarehouseCode(
            String warehouseCode) {

        List<Stock> stocks = stockRepository.findByWarehouseCode(
                warehouseCode);

        return stocks.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // @Override
    // @Transactional // 데이터를 변경해야 됨.... 따라서 ReadOnly를 끄고 시작.
    // public void updateStockQuantity(Long stockId, Long amount) {
    //
    // 기존 재고 조회
    // Stock stock = stockRePository.findById(stockId)
    // .orElseThrow(() -> new IllegalArgumentException("해당 재고가 존재하지 않습니다."));
    //
    // 재고 수량 변경
    // stock.setQuantity(stock.getQuantity() + amount.intValue());
    // stock.setUpdatedAt(LocalDateTime.now());
    //
    // 재고 변경 이력 객체 생성
    // StockHistory history = new StockHistory(stock.getStockId(), amount);
    //
    // 이력 저장
    // stockHistoryRepository.save(history);
    // }

    // DTO 변환 메서드
    private StockDto.Response convertToResponseDto(
            Stock stock) {

        StockDto.Response rs = new StockDto.Response();

        rs.setStockId(
                stock.getStockId());

        rs.setProductId(
                stock.getProductId());

        rs.setWarehouseCode(
                stock.getWarehouseCode());

        rs.setQuantity(
                stock.getQuantity());

        rs.setStockStatus(
                stock.getStockStatus());

        rs.setUpdatedAt(
                stock.getUpdatedAt());

        Product product = productRepository.findById(
                stock.getProductId())
                .orElse(null);

        if (product != null) {

            rs.setProductName(
                    product.getProductName());
        }

        Warehouse warehouse = warehouseRepository.findById(
                stock.getWarehouseCode())
                .orElse(null);

        if (warehouse != null) {

            rs.setWarehouseName(
                    warehouse.getWarehouseName());
        }

        return rs;
    }

}
