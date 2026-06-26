package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.DepartmentPermissionProfileResponse;
import com.buyflow.erp.Dto.DepartmentPermissionUpdateRequest;
import com.buyflow.erp.Service.DepartmentPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/departments")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('roles.write') or hasAuthority('ROLE_MANAGE')")
public class DepartmentPermissionController {

    private final DepartmentPermissionService departmentPermissionService;

    @GetMapping("/permission-profiles")
    public ApiResponse<List<DepartmentPermissionProfileResponse>> findProfiles() {
        return ApiResponse.success(
                "Department permission profiles loaded.",
                departmentPermissionService.findProfiles()
        );
    }

    @GetMapping("/{departmentName}/permissions")
    public ApiResponse<List<String>> findPermissions(
            @PathVariable(name = "departmentName") String departmentName
    ) {
        return ApiResponse.success(
                "Department permissions loaded.",
                departmentPermissionService.findPermissionCodes(departmentName)
        );
    }

    @PutMapping("/{departmentName}/permissions")
    public ApiResponse<List<String>> updatePermissions(
            @PathVariable(name = "departmentName") String departmentName,
            @RequestBody DepartmentPermissionUpdateRequest request
    ) {
        return ApiResponse.success(
                "Department permissions saved.",
                departmentPermissionService.replacePermissions(departmentName, request)
        );
    }
}
