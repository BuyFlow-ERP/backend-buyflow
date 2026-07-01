package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.ProductDto;
import com.buyflow.erp.Entity.Product;
import com.buyflow.erp.Repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private static final int MAX_PRODUCT_PAGE_SIZE = 20000;

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void saveProduct(ProductDto.CreateRequest request) {
        validateProductRequest(null, request);

        Product product = new Product();
        applyRequest(product, request);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, ProductDto.CreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "품목을 찾을 수 없습니다. productId=" + productId
                ));

        validateProductRequest(productId, request);

        applyRequest(product, request);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "품목을 찾을 수 없습니다. productId=" + productId
                ));

        product.setUseYn("N");
    }

    @Override
    public PageResponse<ProductDto.ListResponse> searchProducts(
            ProductDto.SearchCondition condition
    ) {
        ProductDto.SearchCondition safeCondition =
                condition == null ? new ProductDto.SearchCondition() : condition;

        int safePage = Math.max(safeCondition.getPage(), 0);

        int safeSize = Math.min(
                Math.max(safeCondition.getSize(), 1),
                MAX_PRODUCT_PAGE_SIZE
);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        String productNo = normalizeText(safeCondition.getItemCode());
        String productName = normalizeText(safeCondition.getItemName());
        String categoryName = normalizeSelect(safeCondition.getCategory());
        String unit = normalizeSelect(safeCondition.getUnit());
        String useYn = convertActiveStatusToUseYn(safeCondition.getActiveStatus());

        Page<ProductDto.ListResponse> productPage = productRepository
                .searchProducts(productNo, productName, categoryName, unit, useYn, pageable)
                .map(ProductDto.ListResponse::from);

        return PageResponse.from(productPage);
    }

    @Override
    public ProductDto.ListResponse getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(ProductDto.ListResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        "품목을 찾을 수 없습니다. productId=" + productId
                ));
    }

    @Override
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> result = new LinkedHashMap<>();

        List<String> categories = new ArrayList<>();
        categories.add("전체");
        categories.addAll(productRepository.findDistinctCategoryNames());

        List<String> units = new ArrayList<>();
        units.add("전체");
        units.addAll(productRepository.findDistinctUnits());

        result.put("categories", categories);
        result.put("units", units);
        result.put("activeStatuses", List.of("전체", "사용", "미사용"));

        return result;
    }

    private void validateProductRequest(Long productId, ProductDto.CreateRequest request) {
        String productNo = firstNotBlank(request.getProductNo(), request.getCode());
        String productName = firstNotBlank(request.getProductName(), request.getName());
        String categoryName = firstNotBlank(request.getCategoryName(), request.getCategory());
        String unit = normalizeText(request.getUnit());

        if (productNo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "품목 코드는 필수입니다.");
        }

        if (productName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "품목명은 필수입니다.");
        }

        if (categoryName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리는 필수입니다.");
        }

        if (unit == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "단위는 필수입니다.");
        }

        boolean duplicated = productId == null
                ? productRepository.existsByProductNo(productNo)
                : productRepository.existsByProductNoAndProductIdNot(productNo, productId);

        if (duplicated) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 품목 코드입니다.");
        }
    }

    private void applyRequest(Product product, ProductDto.CreateRequest request) {
        product.setProductNo(firstNotBlank(request.getProductNo(), request.getCode()));
        product.setProductName(firstNotBlank(request.getProductName(), request.getName()));
        product.setCompanyName(firstNotBlank(request.getCompanyName(), request.getManufacturer()));
        product.setCategoryName(firstNotBlank(request.getCategoryName(), request.getCategory()));

        product.setBizRegNo(normalizeText(request.getBizRegNo()));
        product.setParentCategory(normalizeText(request.getParentCategory()));

        product.setUnit(normalizeText(request.getUnit()));
        product.setUnitPrice(request.getUnitPrice() == null ? 0L : Math.max(0L, request.getUnitPrice()));
        product.setSpec(normalizeText(request.getSpec()));

        product.setOrigin(normalizeText(request.getOrigin()));
        product.setDescription(normalizeText(request.getDescription()));

        product.setCompetingProduct(resolveYn(request.getCompetingProduct()));

        product.setValidStartDate(request.getValidStartDate());
        product.setValidEndDate(request.getValidEndDate());

        product.setUseYn(resolveUseYn(request));
    }

    private String resolveYn(String value) {
        return "Y".equalsIgnoreCase(normalizeText(value)) ? "Y" : "N";
    }

    private String resolveUseYn(ProductDto.CreateRequest request) {
        if (request.getIsActive() != null) {
            return Boolean.FALSE.equals(request.getIsActive()) ? "N" : "Y";
        }

        String useYn = normalizeText(request.getUseYn());

        if ("N".equalsIgnoreCase(useYn)) {
            return "N";
        }

        return "Y";
    }

    private String convertActiveStatusToUseYn(String activeStatus) {
        String value = normalizeSelect(activeStatus);

        if (value == null) {
            return null;
        }

        if ("사용".equals(value)) {
            return "Y";
        }

        if ("미사용".equals(value)) {
            return "N";
        }

        return null;
    }

    private String firstNotBlank(String first, String second) {
        String normalizedFirst = normalizeText(first);

        if (normalizedFirst != null) {
            return normalizedFirst;
        }

        return normalizeText(second);
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeSelect(String value) {
        if (value == null || value.trim().isEmpty() || "전체".equals(value.trim())) {
            return null;
        }

        return value.trim();
    }
}