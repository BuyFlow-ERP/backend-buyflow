package com.buyflow.erp.Controller;

import com.buyflow.erp.Dto.DashboardDto;
import com.buyflow.erp.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto.Response> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}