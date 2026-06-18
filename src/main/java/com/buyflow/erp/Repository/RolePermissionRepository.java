package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleIdIn(Collection<Long> roleIds);

    // 단일 역할의 권한 매핑 조회
    List<RolePermission> findByRoleId(Long roleId);

    // 단일 역할의 권한 매핑 전체 삭제
    void deleteByRoleId(Long roleId);
}