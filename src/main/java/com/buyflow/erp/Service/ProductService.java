package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.ProductDto;

import java.util.Map;
import java.util.List;

public interface ProductService {

    void saveProduct(ProductDto.CreateRequest request);

    void deleteProduct(Long productId);

    void updateProduct(Long productId, ProductDto.CreateRequest request);

    PageResponse<ProductDto.ListResponse> searchProducts(
            ProductDto.SearchCondition condition
    );

    ProductDto.ListResponse getProduct(Long productId);

    Map<String, Object> getFilterOptions();
    
    List<ProductDto.ListResponse> getProductsForExcel(ProductDto.SearchCondition condition);
}