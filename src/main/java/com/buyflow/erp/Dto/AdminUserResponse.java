package com.buyflow.erp.Dto;

import java.util.List;

public record AdminUserResponse(
        UserResponse user,
        List<RoleResponse> roles,
        List<String> permissions
) {
}
