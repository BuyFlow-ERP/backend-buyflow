package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.UserDepartmentAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDepartmentAuthorizationRepository extends JpaRepository<UserDepartmentAuthorization, Long> {

    Optional<UserDepartmentAuthorization> findByUserId(Long userId);

    @Query("""
            select count(authorization)
            from UserDepartmentAuthorization authorization, User user
            where authorization.userId = user.userId
              and user.useYn = 'Y'
              and trim(authorization.departmentName) = :departmentName
              and authorization.authorizedYn = :authorizedYn
            """)
    long countActiveAuthorizedUsersByDepartmentName(
            @Param("departmentName") String departmentName,
            @Param("authorizedYn") String authorizedYn
    );
}
