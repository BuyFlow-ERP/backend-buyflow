package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Entity.Product;

import java.util.List;

public interface ProductService {

    void saveProduct(ProductDto.CreateRequest request);
    void deleteProduct(Long productId);
    void updateProduct(Long productId,
                   ProductDto.CreateRequest request);

    List<Product> getProducts();
    List<Product> searchProducts(
        String productNo,
        String productName
);
}