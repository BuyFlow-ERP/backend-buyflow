package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
    value = """
        SELECT p
        FROM Product p
        WHERE (:productNo IS NULL OR :productNo = ''
               OR LOWER(p.productNo) LIKE LOWER(CONCAT(:productNo, '%')))
          AND (:productName IS NULL OR :productName = ''
               OR LOWER(p.productName) LIKE LOWER(CONCAT(CONCAT('%', :productName), '%')))
          AND (:categoryName IS NULL OR :categoryName = ''
               OR TRIM(p.categoryName) = :categoryName)
          AND (:unit IS NULL OR :unit = ''
               OR TRIM(p.unit) = :unit)
          AND (:useYn IS NULL OR :useYn = ''
               OR p.useYn = :useYn)
        ORDER BY p.productId DESC
    """,
    countQuery = """
        SELECT COUNT(p)
        FROM Product p
        WHERE (:productNo IS NULL OR :productNo = ''
               OR LOWER(p.productNo) LIKE LOWER(CONCAT(:productNo, '%')))
          AND (:productName IS NULL OR :productName = ''
               OR LOWER(p.productName) LIKE LOWER(CONCAT(CONCAT('%', :productName), '%')))
          AND (:categoryName IS NULL OR :categoryName = ''
               OR TRIM(p.categoryName) = :categoryName)
          AND (:unit IS NULL OR :unit = ''
               OR TRIM(p.unit) = :unit)
          AND (:useYn IS NULL OR :useYn = ''
               OR p.useYn = :useYn)
    """
)
Page<Product> searchProducts(
        @Param("productNo") String productNo,
        @Param("productName") String productName,
        @Param("categoryName") String categoryName,
        @Param("unit") String unit,
        @Param("useYn") String useYn,
        Pageable pageable
);

     @Query(
          value = """
          SELECT category_name
          FROM (
            SELECT DISTINCT TRIM(CATEGORY_NAME) AS category_name
            FROM PRODUCTS
            WHERE TRIM(CATEGORY_NAME) IS NOT NULL
        )
        ORDER BY category_name
    """,
    nativeQuery = true
)
List<String> findDistinctCategoryNames();

     @Query(
          value = """
          SELECT unit_name
          FROM (
            SELECT DISTINCT TRIM(UNIT) AS unit_name
            FROM PRODUCTS
            WHERE TRIM(UNIT) IS NOT NULL
        )
        ORDER BY unit_name
    """,
     nativeQuery = true
)
     List<String> findDistinctUnits();
}