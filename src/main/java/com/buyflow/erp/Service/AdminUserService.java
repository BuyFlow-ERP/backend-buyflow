package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Dto.AdminUserProfileUpdateRequest;
import com.buyflow.erp.Dto.AdminUserResponse;
import com.buyflow.erp.Dto.AdminUserRoleUpdateRequest;
import com.buyflow.erp.Dto.AdminUserStatusUpdateRequest;
import com.buyflow.erp.Dto.PageResponse;
import com.buyflow.erp.Dto.RoleResponse;
import com.buyflow.erp.Dto.UserResponse;
import com.buyflow.erp.Entity.Role;
import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Entity.UserRole;
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.AuthUserRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final String JOB_RANK_ADMIN = "ADMIN";
    private static final String JOB_RANK_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_REQUESTER = "REQUESTER";

    private final AuthUserRepository userRepository;
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
    public PageResponse<AdminUserResponse> search(
            String keyword,
            String status,
            String useYn,
            String jobRank,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                normalizeSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return PageResponse.from(userRepository.search(
                normalizeText(keyword),
                normalizeText(status),
                normalizeText(useYn),
                normalizeJobRankFilter(jobRank),
                pageRequest
        ).map(this::toAdminUserResponse));
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
    public AdminUserResponse updateProfile(
            Long userId,
            AdminUserProfileUpdateRequest request,
            String currentLoginId
    ) {
        User user = findUser(userId);

        if (request.departmentName() != null) {
            user.setDepartmentName(normalizeText(request.departmentName()));
        }

        if (request.positionName() != null) {
            user.setPositionName(normalizeText(request.positionName()));
        }

        
        user.setUpdatedAt(LocalDateTime.now());
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse updateRoles(Long userId, AdminUserRoleUpdateRequest request, String currentLoginId) {
        User user = findUser(userId);
        Set<Long> roleIds = new LinkedHashSet<>(request.roleIds());

        if (roleIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "역할을 하나 이상 선택해야 합니다.");
        }

        List<Role> roles = roleRepository.findByRoleIdInAndUseYnOrderBySortOrderAscRoleCodeAsc(roleIds, "Y");

        if (roles.size() != roleIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "유효하지 않거나 비활성화된 역할이 포함되어 있습니다.");
        }

        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> ROLE_ADMIN.equalsIgnoreCase(role.getRoleCode()));

        if (!hasAdminRole && isSameUser(user, currentLoginId) && hasRole(user.getUserId(), ROLE_ADMIN)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "본인 관리자 권한은 직접 해제할 수 없습니다.");
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

    private void syncRolesByJobRank(User user, String currentLoginId) {
        String jobRank = normalizeJobRank(user.getJobRank());
        user.setJobRank(jobRank);

        if (JOB_RANK_ADMIN.equals(jobRank)) {
            ensureRole(user.getUserId(), ROLE_ADMIN);
            return;
        }

        if (isSameUser(user, currentLoginId) && hasRole(user.getUserId(), ROLE_ADMIN)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "본인 관리자 직급은 직접 낮출 수 없습니다.");
        }

        removeRole(user.getUserId(), ROLE_ADMIN);
        ensureRole(user.getUserId(), ROLE_REQUESTER);
    }

    private void ensureRole(Long userId, String roleCode) {
        Role role = findActiveRole(roleCode);

        if (userRoleRepository.existsByUserIdAndRoleId(userId, role.getRoleId())) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getRoleId());
        userRole.setCreatedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);
    }

    private void removeRole(Long userId, String roleCode) {
        roleRepository.findByRoleCodeAndUseYn(roleCode, "Y")
                .ifPresent(role -> userRoleRepository.deleteByUserIdAndRoleId(userId, role.getRoleId()));
    }

    private boolean hasRole(Long userId, String roleCode) {
        return roleRepository.findByRoleCodeAndUseYn(roleCode, "Y")
                .map(role -> userRoleRepository.existsByUserIdAndRoleId(userId, role.getRoleId()))
                .orElse(false);
    }

    private Role findActiveRole(String roleCode) {
        return roleRepository.findByRoleCodeAndUseYn(roleCode, "Y")
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "활성화된 역할을 찾을 수 없습니다: " + roleCode
                ));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
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

    private boolean isSameUser(User user, String currentLoginId) {
        return StringUtils.hasText(currentLoginId) && currentLoginId.equals(user.getLoginId());
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }

    private String normalizeJobRank(String jobRank) {
        if (!StringUtils.hasText(jobRank)) {
            return JOB_RANK_USER;
        }

        return JOB_RANK_ADMIN.equalsIgnoreCase(jobRank.trim()) ? JOB_RANK_ADMIN : JOB_RANK_USER;
    }

    private String normalizeJobRankFilter(String jobRank) {
        if (!StringUtils.hasText(jobRank)) {
            return null;
        }

        return normalizeJobRank(jobRank);
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return 20;
        }

        return Math.min(size, 100);
    }
}
