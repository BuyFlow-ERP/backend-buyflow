package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.Permission;
import com.buyflow.erp.Entity.Role;
import com.buyflow.erp.Entity.RolePermission;
import com.buyflow.erp.Entity.UserRole;
import com.buyflow.erp.Repository.PermissionRepository;
import com.buyflow.erp.Repository.RolePermissionRepository;
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RbacQueryService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

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
        List<Long> roleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            return List.of();
        }

        List<Long> permissionIds = rolePermissionRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();

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
}

