package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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