package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByPermissionIdInAndUseYnOrderByPermissionGroupAscPermissionCodeAsc(Collection<Long> permissionIds, String useYn);

    List<Permission> findByUseYnOrderByPermissionGroupAscPermissionCodeAsc(String useYn);

    // [추가] 권한 코드 목록으로 활성 권한 조회 (역할-권한 저장 시 코드 -> 엔티티 변환용)
    List<Permission> findByPermissionCodeInAndUseYn(Collection<String> permissionCodes, String useYn);
}