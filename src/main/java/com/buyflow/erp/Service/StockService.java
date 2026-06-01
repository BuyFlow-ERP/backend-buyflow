@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 최적화
public interface StockService {
    // 재고+이력 처리
    //StockHistoryRepository 주입 받아서 처리...
    List<StockDto.Response> findAllStocks();


}
