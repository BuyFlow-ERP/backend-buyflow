package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductNoContaining(String productNo);

    List<Product> findByProductNameContaining(String productName);

    List<Product> findByProductNoContainingAndProductNameContaining(
            String productNo,
            String productName
    );
}