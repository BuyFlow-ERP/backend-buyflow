package com.buyflow.erp.Dto;

public record DepartmentPermissionProfileResponse(
        String departmentName,
        Long userCount,
        Long authorizedUserCount
) {
}
