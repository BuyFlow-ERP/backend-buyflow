package com.buyflow.erp.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    boolean existsByWarehouseCode(String warehouseCode);

    @Query(
        value = """
            SELECT w
            FROM Warehouse w
            LEFT JOIN w.user u
            WHERE (:name IS NULL OR :name = ''
                   OR w.warehouseName LIKE CONCAT(CONCAT('%', :name), '%'))
              AND (:type IS NULL OR :type = ''
                   OR w.type = :type)
              AND (:useYn IS NULL OR :useYn = ''
                   OR w.useYn = :useYn)
              AND (:managerName IS NULL OR :managerName = ''
                   OR u.userName LIKE CONCAT(CONCAT('%', :managerName), '%'))
            ORDER BY w.warehouseCode DESC
        """,
        countQuery = """
            SELECT COUNT(w)
            FROM Warehouse w
            LEFT JOIN w.user u
            WHERE (:name IS NULL OR :name = ''
                   OR w.warehouseName LIKE CONCAT(CONCAT('%', :name), '%'))
              AND (:type IS NULL OR :type = ''
                   OR w.type = :type)
              AND (:useYn IS NULL OR :useYn = ''
                   OR w.useYn = :useYn)
              AND (:managerName IS NULL OR :managerName = ''
                   OR u.userName LIKE CONCAT(CONCAT('%', :managerName), '%'))
        """
    )
    Page<Warehouse> searchByFlexibleCondition(
            @Param("name") String name,
            @Param("type") String type,
            @Param("useYn") String useYn,
            @Param("managerName") String managerName,
            Pageable pageable
    );
}
