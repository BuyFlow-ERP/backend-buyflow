package com.buyflow.erp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.buyflow.erp.Entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginId(String loginId);

    @Query(value = """
        SELECT u.USER_ID
        FROM USERS u
        JOIN USER_ROLES ur
          ON u.USER_ID = ur.USER_ID
        JOIN ROLES r
          ON ur.ROLE_ID = r.ROLE_ID
        WHERE u.USER_ID <> :requestorId
          AND NVL(u.USE_YN, 'Y') = 'Y'
          AND NVL(u.STATUS, 'ACTIVE') = 'ACTIVE'
          AND NVL(r.USE_YN, 'Y') = 'Y'
          AND UPPER(r.ROLE_CODE) = 'APPROVER'
        ORDER BY u.USER_ID
        FETCH FIRST 1 ROWS ONLY
        """, nativeQuery = true)
    Optional<Long> findFirstApproverId(@Param("requestorId") Long requestorId);

    @Query(value = """
        SELECT COUNT(*)
        FROM USER_ROLES ur
        JOIN ROLES r
          ON r.ROLE_ID = ur.ROLE_ID
        WHERE ur.USER_ID = :userId
          AND NVL(r.USE_YN, 'Y') = 'Y'
          AND UPPER(r.ROLE_CODE) = UPPER(:roleCode)
        """, nativeQuery = true)
    long countActiveRoleByUserId(
            @Param("userId") Long userId,
            @Param("roleCode") String roleCode
    );

    @Query(value = """
        SELECT COUNT(*)
        FROM USER_ROLES ur
        JOIN ROLES r
          ON r.ROLE_ID = ur.ROLE_ID
        JOIN ROLE_PERMISSIONS rp
          ON rp.ROLE_ID = r.ROLE_ID
        JOIN PERMISSIONS p
          ON p.PERMISSION_ID = rp.PERMISSION_ID
        WHERE ur.USER_ID = :userId
          AND NVL(r.USE_YN, 'Y') = 'Y'
          AND NVL(p.USE_YN, 'Y') = 'Y'
          AND LOWER(p.PERMISSION_CODE) = LOWER(:permissionCode)
        """, nativeQuery = true)
    long countActivePermissionByUserId(
            @Param("userId") Long userId,
            @Param("permissionCode") String permissionCode
    );
}