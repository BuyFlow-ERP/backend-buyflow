package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findFirstByUserNameAndEmailAndPhoneAndUseYn(String userName, String email, String phone, String useYn);

    Optional<User> findFirstByLoginIdAndUserNameAndEmailAndPhoneAndUseYn(
            String loginId,
            String userName,
            String email,
            String phone,
            String useYn
    );

    boolean existsByLoginId(String loginId);

    List<User> findAllByOrderByCreatedAtDesc();

    @Query("""
            select user
            from User user
            where trim(user.departmentName) = :departmentName
            order by user.createdAt desc
            """)
    List<User> findByDepartmentNameScoped(@Param("departmentName") String departmentName);

    @Query("""
            select distinct user.departmentName
            from User user
            where user.departmentName is not null
            order by user.departmentName
            """)
    List<String> findDistinctDepartmentNames();

    @Query("""
            select count(user)
            from User user
            where user.userId <> :userId
              and user.useYn = 'Y'
              and trim(user.departmentName) = :departmentName
              and (user.jobRank like concat(concat('%', :leaderKeyword), '%')
                   or user.positionName like concat(concat('%', :leaderKeyword), '%'))
            """)
    long countActiveDepartmentLeaders(
            @Param("departmentName") String departmentName,
            @Param("leaderKeyword") String leaderKeyword,
            @Param("userId") Long userId
    );

    @Query("""
            select user
            from User user
            where (:keyword is null
                   or lower(user.loginId) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.userName) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.email) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.phone) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.departmentName) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.positionName) like lower(concat(concat('%', :keyword), '%')))
              and (:departmentName is null or trim(user.departmentName) = :departmentName)
              and (:status is null or user.status = :status)
              and (:useYn is null or user.useYn = :useYn)
              and (:jobRank is null or user.jobRank = :jobRank)
              and (:roleCode is null or exists (
                    select userRole.userRoleId
                    from UserRole userRole, Role role
                    where userRole.userId = user.userId
                      and role.roleId = userRole.roleId
                      and role.roleCode = :roleCode
                      and role.useYn = 'Y'
              ))
            """)
    Page<User> search(
            @Param("keyword") String keyword,
            @Param("departmentName") String departmentName,
            @Param("status") String status,
            @Param("useYn") String useYn,
            @Param("jobRank") String jobRank,
            @Param("roleCode") String roleCode,
            Pageable pageable
    );
}
