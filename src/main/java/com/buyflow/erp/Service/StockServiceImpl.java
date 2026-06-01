public class StockServiceImpl implements StockService {

    private final StockRePository StockRePository;
    // private final ProductRepository productRepository;
    // private final WarehouseRepository warehouseRepository;

    @Override
    public List<StockDto.Response> findAllStocks() {
        // DB에서 재고 엔티티 전체 리스트 조회
        List<Stock> stocks = StockRePository.findAll();

        // 엔티티 리스트를 화면용 Response DTO List로 변환 
        return stocks.stream()
                .map(this::convertToResponseDto);
                .collect(Collectors.toList());
    }
    
    private StockDto.Response convertToResponseDto(Stock stock) {
        StockDto.Response rs = new StockDto.Response();

        rs.setStockId(stock.getStockId());
        rs.setProductId(stock.getProductId());
        rs.setWarehouseCode(stock.getWarehouseCode());
        rs.setQuantity(stock.getQuantity());
        rs.setStockStatus(stock.getStockStatus());
        rs.setUpdatedAt(stock.getUpdatedAt());

        rs.setProductName("품목명");
        rs.setWarehouseName("창고명");

        return rs;

    }
    
}
