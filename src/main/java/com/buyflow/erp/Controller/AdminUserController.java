package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.AdminUserProfileUpdateRequest;
import com.buyflow.erp.Dto.AdminUserResponse;
import com.buyflow.erp.Dto.AdminUserRoleUpdateRequest;
import com.buyflow.erp.Dto.AdminUserStatusUpdateRequest;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_MANAGE')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> findAll() {
        return ApiResponse.success("관리자 사용자 목록 조회 성공", adminUserService.findAll());
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<AdminUserResponse>> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "useYn", required = false) String useYn,
            @RequestParam(name = "jobRank", required = false) String jobRank,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ApiResponse.success(
                "관리자 사용자 목록 조회 성공",
                adminUserService.search(keyword, status, useYn, jobRank, page, size)
        );
    }

    @GetMapping("/{userId}")
    public ApiResponse<AdminUserResponse> findById(@PathVariable(name = "userId") Long userId) {
        return ApiResponse.success("관리자 사용자 상세 조회 성공", adminUserService.findById(userId));
    }

    @PatchMapping("/{userId}/approve")
    public ApiResponse<AdminUserResponse> approve(@PathVariable(name = "userId") Long userId) {
        return ApiResponse.success("사용자 승인 성공", adminUserService.approve(userId));
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<AdminUserResponse> updateStatus(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody AdminUserStatusUpdateRequest request
    ) {
        return ApiResponse.success("사용자 상태 수정 성공", adminUserService.updateStatus(userId, request));
    }

    @PutMapping("/{userId}/profile")
    public ApiResponse<AdminUserResponse> updateProfile(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody AdminUserProfileUpdateRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "사용자 조직 정보 수정 성공",
                adminUserService.updateProfile(userId, request, authentication.getName())
        );
    }

    @PutMapping("/{userId}/roles")
    public ApiResponse<AdminUserResponse> updateRoles(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "사용자 역할 수정 성공",
                adminUserService.updateRoles(userId, request, authentication.getName())
        );
    }
}
