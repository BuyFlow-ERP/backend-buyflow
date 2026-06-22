package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ReceiptDto;
import com.buyflow.erp.Entity.Receipt;
import com.buyflow.erp.Service.ReceiptService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/receipts", "/api/receipts"})
public class ReceiptController {

    private final ReceiptService receiptService;

    /*
     * 입고 목록 조회
     * 프론트에서 호출:
     * GET /api/receipts?activeTab=EXPECTED&page=1&size=10...
     */
    @GetMapping
    public ResponseEntity<ReceiptDto.PageResponse<ReceiptDto.ListResponse>> getReceipts(
            @RequestParam(name = "activeTab", required = false) String activeTab,
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "supplierKeyword", required = false) String supplierKeyword,
            @RequestParam(name = "itemKeyword", required = false) String itemKeyword,
            @RequestParam(name = "warehouseName", required = false) String warehouseName,
            @RequestParam(name = "expectedFrom", required = false) String expectedFrom,
            @RequestParam(name = "expectedTo", required = false) String expectedTo,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        ReceiptDto.PageResponse<ReceiptDto.ListResponse> result =
                receiptService.searchReceipts(
                        activeTab,
                        orderNumber,
                        supplierKeyword,
                        itemKeyword,
                        warehouseName,
                        expectedFrom,
                        expectedTo,
                        status,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }

    /*
     * 입고 관리 검색 조건 조회
     * 프론트에서 호출:
     * GET /api/receipts/filter-options
     */
    @GetMapping("/filter-options")
    public ResponseEntity<ReceiptDto.FilterOptionsResponse> getFilterOptions() {
        return ResponseEntity.ok(receiptService.getFilterOptions());
    }

    /*
     * 입고 관리 상단 요약 카드 조회
     * 프론트에서 호출:
     * GET /api/receipts/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ReceiptDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(receiptService.getSummary());
    }

    /*
     * 입고 상세 조회
     * 중요:
     * 기존 @GetMapping("/{receiptId}")는
     * /filter-options, /summary까지 receiptId로 인식할 수 있음.
     *
     * 그래서 숫자만 받도록 제한해야 함.
     */
    @GetMapping("/{receiptId:\\d+}")
    public ResponseEntity<Receipt> getReceipt(
            @PathVariable(name = "receiptId") Long receiptId
    ) {
        return ResponseEntity.ok(receiptService.getReceipt(receiptId));
    }

    /*
     * 입고 등록
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveReceipt(
            @RequestBody ReceiptDto.ReceiptCreateRequest request
    ) {
        try {
            System.out.println("POST /receipts 호출");

            receiptService.saveReceipt(request);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "저장 완료"
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }

    /*
     * 테스트용 API
     * 필요 없으면 삭제해도 됨.
     */
    @PostMapping("/test")
    public ResponseEntity<String> test(
            @RequestBody ReceiptDto.ReceiptCreateRequest request
    ) {
        return ResponseEntity.ok(request.getReceiptNo());
    }
}