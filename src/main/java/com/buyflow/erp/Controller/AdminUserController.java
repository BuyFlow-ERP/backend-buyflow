package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.AdminUserResponse;
import com.buyflow.erp.Dto.AdminUserRoleUpdateRequest;
import com.buyflow.erp.Dto.AdminUserStatusUpdateRequest;
import com.buyflow.erp.Service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_MANAGE')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> findAll() {
        return ApiResponse.success("Admin user list loaded.", adminUserService.findAll());
    }

    @GetMapping("/{userId}")
    public ApiResponse<AdminUserResponse> findById(@PathVariable Long userId) {
        return ApiResponse.success("Admin user detail loaded.", adminUserService.findById(userId));
    }

    @PatchMapping("/{userId}/approve")
    public ApiResponse<AdminUserResponse> approve(@PathVariable Long userId) {
        return ApiResponse.success("User approved.", adminUserService.approve(userId));
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<AdminUserResponse> updateStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserStatusUpdateRequest request
    ) {
        return ApiResponse.success("User status updated.", adminUserService.updateStatus(userId, request));
    }

    @PutMapping("/{userId}/roles")
    public ApiResponse<AdminUserResponse> updateRoles(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequest request
    ) {
        return ApiResponse.success("User roles updated.", adminUserService.updateRoles(userId, request));
    }
}
