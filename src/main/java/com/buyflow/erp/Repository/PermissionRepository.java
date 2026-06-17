package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByPermissionIdInAndUseYnOrderByPermissionGroupAscPermissionCodeAsc(Collection<Long> permissionIds, String useYn);

    List<Permission> findByUseYnOrderByPermissionGroupAscPermissionCodeAsc(String useYn);
}

