package com.buyflow.erp.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierIdAndUseYn(Long supplierId, String useYn);

    boolean existsBySupplierCode(String supplierCode);

    boolean existsBySupplierCodeAndSupplierIdNot(String supplierCode, Long supplierId);

    boolean existsByBusinessNumber(String businessNumber);

    boolean existsByBusinessNumberAndSupplierIdNot(String businessNumber, Long supplierId);

    @Query("""
            select supplier
            from Supplier supplier
            where supplier.useYn = 'Y'
              and (:supplierCode is null or lower(supplier.supplierCode) like lower(concat(concat('%', :supplierCode), '%')))
              and (:supplierName is null or lower(supplier.supplierName) like lower(concat(concat('%', :supplierName), '%')))
              and (:manager is null or lower(supplier.manager) like lower(concat(concat('%', :manager), '%')))
              and (:tradeStatus is null or supplier.tradeStatus = :tradeStatus)
            """)
    Page<Supplier> search(
            @Param("supplierCode") String supplierCode,
            @Param("supplierName") String supplierName,
            @Param("manager") String manager,
            @Param("tradeStatus") String tradeStatus,
            Pageable pageable
    );
}
