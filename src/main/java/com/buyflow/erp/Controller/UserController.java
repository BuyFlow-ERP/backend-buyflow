package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.UserResponse;
import com.buyflow.erp.Dto.UserUpdateRequest;
import com.buyflow.erp.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> findAll() {
        return ApiResponse.success("사용자 목록 조회 성공", userService.findAll());
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<UserResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String useYn,
            @RequestParam(required = false) String jobRank,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(
                "사용자 목록 조회 성공",
                userService.search(keyword, status, useYn, jobRank, page, size)
        );
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> findById(@PathVariable Long userId) {
        return ApiResponse.success("사용자 상세 조회 성공", userService.findById(userId));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "사용자 정보 수정 성공",
                userService.update(userId, request, authentication.getName(), canManageUsers(authentication))
        );
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long userId, Authentication authentication) {
        userService.deactivate(userId, authentication.getName(), canManageUsers(authentication));
    }

    private boolean canManageUsers(Authentication authentication) {
        return authentication != null && authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority) || "USER_MANAGE".equals(authority));
    }
}
