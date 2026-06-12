package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void saveProduct(ProductDto.CreateRequest request) {

        Product product = new Product();

        product.setProductId(request.getProductId());
        product.setProductNo(request.getProductNo());
        product.setProductName(request.getProductName());
        product.setCompanyName(request.getCompanyName());
        product.setUnitPrice(request.getUnitPrice());
        product.setUnit(request.getUnit());
        product.setCategoryName(request.getCategoryName());
        product.setSpec(request.getSpec());

        productRepository.save(product);
    }
    @Override
public List<Product> getProducts() {
    return productRepository.findAll();
}
@Override
public void updateProduct(
        Long productId,
        ProductDto.CreateRequest request) {

    Product product =
            productRepository.findById(productId)
                    .orElseThrow();

    product.setProductNo(request.getProductNo());
    product.setProductName(request.getProductName());
    product.setCompanyName(request.getCompanyName());
    product.setUnitPrice(request.getUnitPrice());
    product.setUnit(request.getUnit());
    product.setCategoryName(request.getCategoryName());
    product.setSpec(request.getSpec());

    productRepository.save(product);
}
@Override
public void deleteProduct(Long productId) {

    Product product =
            productRepository.findById(productId)
                    .orElseThrow();

    productRepository.delete(product);
}
@Override
public List<Product> searchProducts(
        String productNo,
        String productName
) {

    if (!productNo.isEmpty() && !productName.isEmpty()) {
        return productRepository
                .findByProductNoContainingAndProductNameContaining(
                        productNo,
                        productName
                );
    }

    if (!productNo.isEmpty()) {
        return productRepository.findByProductNoContaining(productNo);
    }

    if (!productName.isEmpty()) {
        return productRepository.findByProductNameContaining(productName);
    }

    return productRepository.findAll();
}
}