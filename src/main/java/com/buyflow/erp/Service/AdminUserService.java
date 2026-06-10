package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.AdminUserResponse;
import com.buyflow.erp.Dto.AdminUserRoleUpdateRequest;
import com.buyflow.erp.Dto.AdminUserStatusUpdateRequest;
import com.buyflow.erp.Dto.RoleResponse;
import com.buyflow.erp.Dto.UserResponse;
import com.buyflow.erp.Entity.Role;
import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Entity.UserRole;
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.UserRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RbacQueryService rbacQueryService;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> findAll() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserResponse findById(Long userId) {
        User user = findUser(userId);
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse approve(Long userId) {
        User user = findUser(userId);
        user.setStatus("ACTIVE");
        user.setUseYn("Y");
        user.setUpdatedAt(LocalDateTime.now());
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse updateStatus(Long userId, AdminUserStatusUpdateRequest request) {
        User user = findUser(userId);
        user.setStatus(request.status());

        if (request.useYn() != null) {
            user.setUseYn(request.useYn());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse updateRoles(Long userId, AdminUserRoleUpdateRequest request) {
        User user = findUser(userId);
        Set<Long> roleIds = new LinkedHashSet<>(request.roleIds());

        if (roleIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "At least one role is required.");
        }

        List<Role> roles = roleRepository.findByRoleIdInAndUseYnOrderBySortOrderAscRoleCodeAsc(roleIds, "Y");

        if (roles.size() != roleIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid or inactive role is included.");
        }

        LocalDateTime now = LocalDateTime.now();
        userRoleRepository.deleteByUserId(userId);

        List<UserRole> userRoles = roles.stream()
                .map(role -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(role.getRoleId());
                    userRole.setCreatedAt(now);
                    return userRole;
                })
                .toList();

        userRoleRepository.saveAll(userRoles);
        user.setUpdatedAt(now);
        return toAdminUserResponse(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found."));
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        List<Long> roleIds = userRoleRepository.findByUserId(user.getUserId())
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        List<RoleResponse> roles = roleIds.isEmpty()
                ? List.of()
                : roleRepository.findByRoleIdInAndUseYnOrderBySortOrderAscRoleCodeAsc(roleIds, "Y")
                        .stream()
                        .map(RoleResponse::from)
                        .toList();

        return new AdminUserResponse(
                UserResponse.from(user),
                roles,
                rbacQueryService.findPermissionCodesByUserId(user.getUserId())
        );
    }
}
