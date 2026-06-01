public interface StockService {

    // 전체 조회
    List<StockDto.Response> findAllStocks();

    // 재고 수량 변경 및 이력 쌓기
    void updateStockQuantity(Long stockId, Long amount);


}
