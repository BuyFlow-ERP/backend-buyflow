package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.WarehouseDto;

public interface WarehouseService {

	PageResponse<WarehouseDto.HouseList> searchWarehouses(WarehouseDto.SearchCondition condition);

	List<WarehouseDto.HouseList> findAllWarehouses();

	WarehouseDto.Detail getWarehouse(String warehouseCode);
	
	WarehouseDto.Create createWarehouse(WarehouseDto.Create request);

	void deleteWarehouse(String warehouseCode);
	
	WarehouseDto.Detail updateWarehouse(String warehouseCode, WarehouseDto.Update request);
}
