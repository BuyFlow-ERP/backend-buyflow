package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.DepartmentRoleAssignmentRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRoleAssignmentRuleRepository extends JpaRepository<DepartmentRoleAssignmentRule, Long> {

    @Query("""
            select rule.roleCode
            from DepartmentRoleAssignmentRule rule
            where trim(rule.departmentName) = :departmentName
              and rule.useYn = :useYn
            order by rule.sortOrder asc, rule.roleCode asc
            """)
    List<String> findRoleCodesByDepartmentName(
            @Param("departmentName") String departmentName,
            @Param("useYn") String useYn
    );

    List<DepartmentRoleAssignmentRule> findByUseYnOrderByDepartmentNameAscSortOrderAscRoleCodeAsc(String useYn);
}
