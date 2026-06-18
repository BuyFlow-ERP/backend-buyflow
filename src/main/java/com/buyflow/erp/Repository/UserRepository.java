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
        JOIN USER_ROLES ur ON u.USER_ID = ur.USER_ID
        JOIN ROLES r ON ur.ROLE_ID = r.ROLE_ID
        WHERE u.USER_ID <> :requestorId
          AND u.USE_YN = 'Y'
          AND NVL(u.STATUS, 'ACTIVE') = 'ACTIVE'
          AND r.USE_YN = 'Y'
          AND r.ROLE_CODE = 'APPROVER'
        ORDER BY u.USER_ID
        FETCH FIRST 1 ROWS ONLY
        """, nativeQuery = true)
Optional<Long> findFirstApproverId(@Param("requestorId") Long requestorId);}