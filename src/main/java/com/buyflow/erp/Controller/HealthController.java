package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success("BuyFlow ERP backend is running.", Map.of(
                "status", "UP",
                "checkedAt", LocalDateTime.now()
        ));
    }
}

