package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>{
	
	boolean existsByWarehouseCode(String warehouseCode);

	@Query("SELECT w FROM Warehouse w WHERE " +
           "(:name IS NULL OR w.warehouseName LIKE CONCAT('%', CONCAT(:name, '%'))) AND " +
           "(:type IS NULL OR w.type = :type) AND " +
           "(:useYn IS NULL OR w.useYn = :useYn)")
    	List<Warehouse> searchByFlexibleCondition(
            @Param("name") String name, 
            @Param("type") String type, 
            @Param("useYn") String useYn
    	);
    
}
