package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> saveProduct(
            @RequestBody ProductDto.CreateRequest request
    ) {
        productService.saveProduct(request);

        return ResponseEntity.ok("저장 완료");
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto.ListResponse>> getProducts(
            @ModelAttribute ProductDto.SearchCondition condition
    ) {
        return ResponseEntity.ok(productService.searchProducts(condition));
    }

    @GetMapping("/excel")
    public void downloadProductsExcel(
        @ModelAttribute ProductDto.SearchCondition condition,
        HttpServletResponse response
    ) throws IOException {
        List<ProductDto.ListResponse> products =
            productService.getProductsForExcel(condition);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("품목 관리");

            String[] headers = {
                    "품목 코드",
                    "품목명",
                    "카테고리",
                    "규격",
                    "단위",
                    "기준 단가",
                    "제조사",
                    "사용 여부",
                    "등록일",
                    "수정일"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;

            for (ProductDto.ListResponse product : products) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(valueOrDash(product.getProductNo()));
                row.createCell(1).setCellValue(valueOrDash(product.getProductName()));
                row.createCell(2).setCellValue(valueOrDash(product.getCategoryName()));
                row.createCell(3).setCellValue(valueOrDash(product.getSpec()));
                row.createCell(4).setCellValue(valueOrDash(product.getUnit()));
                row.createCell(5).setCellValue(
                        product.getUnitPrice() != null ? product.getUnitPrice() : 0
                );
                row.createCell(6).setCellValue(valueOrDash(product.getCompanyName()));
                row.createCell(7).setCellValue(
                        "N".equalsIgnoreCase(product.getUseYn()) ? "미사용" : "사용"
                );
                row.createCell(8).setCellValue(valueOrDash(product.getCreatedAt()));
                row.createCell(9).setCellValue(valueOrDash(product.getUpdatedAt()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = URLEncoder
                    .encode("품목관리.xlsx", StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + fileName
            );

            workbook.write(response.getOutputStream());
        }
    }

    private String valueOrDash(String value) {
    return value != null && !value.isBlank() ? value : "-";
}

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.ListResponse> getProduct(
            @PathVariable(name = "productId") Long productId
    ) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        return ResponseEntity.ok(productService.getFilterOptions());
    }

    @PutMapping("/{productId}")

	public ResponseEntity<String> updateProduct(
	        @PathVariable(name = "productId") Long productId,
	        @RequestBody ProductDto.CreateRequest request) {
	
	        productService.updateProduct(productId, request);
	
	    return ResponseEntity.ok("수정 완료");
	}
    
	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(
	        @PathVariable(name = "productId") Long productId) {
	
	        productService.deleteProduct(productId);
	
	        return ResponseEntity.ok("삭제 완료");
	    }
}