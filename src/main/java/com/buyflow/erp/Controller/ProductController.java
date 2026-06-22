package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            @RequestBody ProductDto.CreateRequest request) {

        productService.saveProduct(request);

        return ResponseEntity.ok("저장 완료");
    }

    @GetMapping
public List<Product> getProducts(

        @RequestParam(name = "itemCode", required = false, defaultValue = "")
        String itemCode,

        @RequestParam(name = "itemName", required = false, defaultValue = "")
        String itemName
) {

    return productService.searchProducts(
            itemCode,
            itemName
    );
}

    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {

        Map<String, Object> result = new HashMap<>();

        result.put("categories", List.of(
                "전체",
                "전자제품",
                "전자기기",
                "사무용품",
                "가구",
                "생활가전",
                "네트워크/통신 장비",
                "기타 상품"
        ));

        result.put("units", List.of(
                "전체",
                "EA",
                "BOX",
                "SET",
                "개",
                "대",
                "장",
                "㎡"
        ));

        result.put("activeStatuses", List.of(
                "전체",
                "사용",
                "미사용"
        ));

        return ResponseEntity.ok(result);
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