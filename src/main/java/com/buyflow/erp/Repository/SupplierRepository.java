package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierIdAndUseYn(Long supplierId, String useYn);

    boolean existsBySupplierCode(String supplierCode);

    boolean existsBySupplierCodeAndSupplierIdNot(String supplierCode, Long supplierId);

    @Query("""
            select s
            from Supplier s
            where s.useYn = 'Y'
              and (:supplierCode is null or lower(s.supplierCode) like lower(concat('%', :supplierCode, '%')))
              and (:supplierName is null or lower(s.supplierName) like lower(concat('%', :supplierName, '%')))
              and (:manager is null or lower(s.manager) like lower(concat('%', :manager, '%')))
              and (:tradeStatus is null or s.tradeStatus = :tradeStatus)
            order by s.createdAt desc, s.supplierId desc
            """)
    Page<Supplier> search(
            @Param("supplierCode") String supplierCode,
            @Param("supplierName") String supplierName,
            @Param("manager") String manager,
            @Param("tradeStatus") String tradeStatus,
            Pageable pageable
    );
}
