package com.buyflow.erp.Service;

import java.util.List;

import com.buyflow.erp.Dto.WarehouseDto;

public interface WarehouseService {
    List<WarehouseDto.HouseList> findAllWarehouses();
}
