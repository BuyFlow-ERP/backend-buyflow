package com.buyflow.erp.Dto;

import java.util.List;

public record DepartmentPermissionUpdateRequest(
        List<String> permissionCodes
) {
}
