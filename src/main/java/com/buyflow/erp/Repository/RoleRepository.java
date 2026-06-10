package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCodeAndUseYn(String roleCode, String useYn);

    List<Role> findByRoleIdInAndUseYnOrderBySortOrderAscRoleCodeAsc(Collection<Long> roleIds, String useYn);

    List<Role> findByUseYnOrderBySortOrderAscRoleCodeAsc(String useYn);
}

