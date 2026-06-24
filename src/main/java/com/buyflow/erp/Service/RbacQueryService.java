package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.Permission;
import com.buyflow.erp.Entity.Role;
import com.buyflow.erp.Entity.RolePermission;
import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Entity.UserDepartmentAuthorization;
import com.buyflow.erp.Entity.UserRole;
import com.buyflow.erp.Repository.AuthUserRepository;
import com.buyflow.erp.Repository.DepartmentPermissionRepository;
import com.buyflow.erp.Repository.PermissionRepository;
import com.buyflow.erp.Repository.RolePermissionRepository;
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.UserDepartmentAuthorizationRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RbacQueryService {

    private final AuthUserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final DepartmentPermissionRepository departmentPermissionRepository;
    private final UserDepartmentAuthorizationRepository userDepartmentAuthorizationRepository;

    public List<String> findRoleCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            return List.of();
        }

        return roleRepository.findByRoleIdInAndUseYnOrderBySortOrderAscRoleCodeAsc(roleIds, "Y")
                .stream()
                .map(Role::getRoleCode)
                .toList();
    }

    public List<String> findPermissionCodesByUserId(Long userId) {
        Set<Long> permissionIds = new LinkedHashSet<>();

        List<Long> roleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        if (!roleIds.isEmpty()) {
            rolePermissionRepository.findByRoleIdIn(roleIds)
                    .stream()
                    .map(RolePermission::getPermissionId)
                    .forEach(permissionIds::add);
        }

        userRepository.findById(userId)
                .filter(this::hasActiveDepartmentAuthorization)
                .map(User::getDepartmentName)
                .map(this::normalizeText)
                .filter(StringUtils::hasText)
                .ifPresent(departmentName -> departmentPermissionRepository
                        .findPermissionIdsByDepartmentName(departmentName)
                        .forEach(permissionIds::add));

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        Set<String> permissionCodes = new LinkedHashSet<>();
        permissionRepository.findByPermissionIdInAndUseYnOrderByPermissionGroupAscPermissionCodeAsc(permissionIds, "Y")
                .stream()
                .map(Permission::getPermissionCode)
                .forEach(permissionCodes::add);

        return List.copyOf(permissionCodes);
    }

    private boolean hasActiveDepartmentAuthorization(User user) {
        String departmentName = normalizeText(user.getDepartmentName());
        if (!StringUtils.hasText(departmentName)) {
            return false;
        }

        return userDepartmentAuthorizationRepository.findByUserId(user.getUserId())
                .filter(authorization -> "Y".equals(authorization.getAuthorizedYn()))
                .map(UserDepartmentAuthorization::getDepartmentName)
                .map(this::normalizeText)
                .filter(departmentName::equals)
                .isPresent();
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}

