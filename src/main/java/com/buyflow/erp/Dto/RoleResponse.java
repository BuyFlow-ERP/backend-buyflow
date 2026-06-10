package com.buyflow.erp.Dto;

import com.buyflow.erp.Entity.Role;

public record RoleResponse(
        Long roleId,
        String roleCode,
        String roleName,
        String roleGroup,
        String description,
        Integer sortOrder,
        String useYn
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getRoleGroup(),
                role.getDescription(),
                role.getSortOrder(),
                role.getUseYn()
        );
    }
}

