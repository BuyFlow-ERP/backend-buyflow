package com.buyflow.erp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // ⭕ jsha님 버전: 연관관계 객체 구조에 맞춘 깔끔한 정석 네이밍 룰
    List<PurchaseOrder> findBySupplier_SupplierId(Long supplierId);

    List<PurchaseOrder> findByOrderStatus(String status);

    @Query("SELECT p FROM PurchaseOrder p LEFT JOIN FETCH p.items WHERE p.orderId = :orderId")
    Optional<PurchaseOrder> findByIdWithItems(@Param("orderId") Long orderId);

    // ⭕ jsha님 버전: 4명 접속은 물론 수백 명도 버티는 무결점 고성능 동적 검색 쿼리!
    @Query("SELECT DISTINCT p FROM PurchaseOrder p " +
            "LEFT JOIN p.supplier s " +
            "LEFT JOIN p.user u " +
            "WHERE (:orderNo IS NULL OR CONCAT(p.orderId, '') LIKE CONCAT('%', CONCAT(:orderNo, '%'))) " +
            "AND (:supplierName IS NULL OR s.supplierName LIKE CONCAT('%', CONCAT(:supplierName, '%'))) " +
            "AND (:userName IS NULL OR u.userName LIKE CONCAT('%', CONCAT(:userName, '%'))) " +
            "AND (:status IS NULL OR p.orderStatus = :status) " +
            "ORDER BY p.orderId DESC")
    
    Page<PurchaseOrder> searchOrdersAdvanced(
            @Param("orderNo") String orderNo,
            @Param("supplierName") String supplierName,
            @Param("userName") String userName,
            @Param("status") String status,
            Pageable pageable
    );
}