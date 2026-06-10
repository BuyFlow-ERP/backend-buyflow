package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.PermissionResponse;
import com.buyflow.erp.Dto.RoleResponse;
import com.buyflow.erp.Service.PermissionService;
import com.buyflow.erp.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_MANAGE')")
public class AdminRbacController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> findRoles() {
        return ApiResponse.success("Admin role list loaded.", roleService.findAll());
    }

    @GetMapping("/permissions")
    public ApiResponse<List<PermissionResponse>> findPermissions() {
        return ApiResponse.success("Admin permission list loaded.", permissionService.findAll());
    }
}
