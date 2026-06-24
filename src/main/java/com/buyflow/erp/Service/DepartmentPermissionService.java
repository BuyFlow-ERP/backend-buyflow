package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.DepartmentPermissionProfileResponse;
import com.buyflow.erp.Dto.DepartmentPermissionUpdateRequest;
import com.buyflow.erp.Entity.DepartmentPermission;
import com.buyflow.erp.Entity.Permission;
import com.buyflow.erp.Repository.AuthUserRepository;
import com.buyflow.erp.Repository.DepartmentPermissionRepository;
import com.buyflow.erp.Repository.PermissionRepository;
import com.buyflow.erp.Repository.UserDepartmentAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentPermissionService {

    private final AuthUserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final DepartmentPermissionRepository departmentPermissionRepository;
    private final UserDepartmentAuthorizationRepository userDepartmentAuthorizationRepository;

    public List<DepartmentPermissionProfileResponse> findProfiles() {
        return userRepository.findDistinctDepartmentNames()
                .stream()
                .map(this::normalizeText)
                .filter(StringUtils::hasText)
                .distinct()
                .map(departmentName -> new DepartmentPermissionProfileResponse(
                        departmentName,
                        userRepository.countActiveUsersByDepartmentName(departmentName),
                        userDepartmentAuthorizationRepository.countActiveAuthorizedUsersByDepartmentName(
                                departmentName,
                                "Y"
                        )
                ))
                .toList();
    }

    public List<String> findPermissionCodes(String departmentName) {
        String normalizedDepartmentName = requireDepartmentName(departmentName);
        List<Long> permissionIds = departmentPermissionRepository
                .findPermissionIdsByDepartmentName(normalizedDepartmentName);

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        return permissionRepository
                .findByPermissionIdInAndUseYnOrderByPermissionGroupAscPermissionCodeAsc(permissionIds, "Y")
                .stream()
                .map(Permission::getPermissionCode)
                .toList();
    }

    @Transactional
    public List<String> replacePermissions(
            String departmentName,
            DepartmentPermissionUpdateRequest request
    ) {
        String normalizedDepartmentName = requireDepartmentName(departmentName);
        Set<String> requestedCodes = new LinkedHashSet<>(request.permissionCodes() == null
                ? List.of()
                : request.permissionCodes());

        List<Permission> permissions = requestedCodes.isEmpty()
                ? List.of()
                : permissionRepository.findByPermissionCodeInAndUseYn(requestedCodes, "Y");

        if (permissions.size() != requestedCodes.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Invalid or inactive permission is included.");
        }

        departmentPermissionRepository.deleteByDepartmentName(normalizedDepartmentName);
        departmentPermissionRepository.flush();

        LocalDateTime now = LocalDateTime.now();
        List<DepartmentPermission> rows = permissions.stream()
                .map(permission -> {
                    DepartmentPermission departmentPermission = new DepartmentPermission();
                    departmentPermission.setDepartmentName(normalizedDepartmentName);
                    departmentPermission.setPermissionId(permission.getPermissionId());
                    departmentPermission.setCreatedAt(now);
                    return departmentPermission;
                })
                .toList();

        departmentPermissionRepository.saveAll(rows);

        return permissions.stream()
                .map(Permission::getPermissionCode)
                .sorted()
                .toList();
    }

    private String requireDepartmentName(String departmentName) {
        String normalized = normalizeText(departmentName);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Department name is required.");
        }

        return normalized;
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}
