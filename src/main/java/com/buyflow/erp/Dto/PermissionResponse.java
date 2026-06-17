package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.Permission;

public record PermissionResponse(
        Long permissionId,
        String permissionCode,
        String permissionName,
        String permissionGroup,
        String description,
        String useYn
) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getPermissionId(),
                permission.getPermissionCode(),
                permission.getPermissionName(),
                permission.getPermissionGroup(),
                permission.getDescription(),
                permission.getUseYn()
        );
    }
}

