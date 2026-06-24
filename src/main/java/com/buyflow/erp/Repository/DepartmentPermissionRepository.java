package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.DepartmentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentPermissionRepository extends JpaRepository<DepartmentPermission, Long> {

    @Query("""
            select departmentPermission.permissionId
            from DepartmentPermission departmentPermission
            where trim(departmentPermission.departmentName) = :departmentName
            order by departmentPermission.permissionId asc
            """)
    List<Long> findPermissionIdsByDepartmentName(@Param("departmentName") String departmentName);

    void deleteByDepartmentName(String departmentName);
}
