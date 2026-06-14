package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>{
	
	boolean existsByWarehouseCode(String warehouseCode);

	List<Warehouse> findByWarehouseTypeContaining(String type);

	List<Warehouse> findByWarehouseNameContaining(String warehouseName);

	List<Warehouse> findByWarehouseTypeContainingAndWarehouseNameContaining(String type, String warehouseName);
    
}
