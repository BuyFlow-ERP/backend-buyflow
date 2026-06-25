package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.DashboardDto;

public interface DashboardService {

    DashboardDto.Response getDashboard(int receiptMonths);
}