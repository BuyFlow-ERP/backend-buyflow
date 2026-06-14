package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.WarehouseDto;

public interface WarehouseService {

	List<WarehouseDto.HouseList> searchWarehouses(WarehouseDto.SearchCondition condition);

	List<WarehouseDto.HouseList> findAllWarehouses();

	WarehouseDto.Detail getWarehouse(String warehouseCode);
	
	void createWarehouse(WarehouseDto.Create request);

	void deleteWarehouse(String warehouseCode);
	
	void updateWarehouse(String warehouseCode, WarehouseDto.Update request);
}
