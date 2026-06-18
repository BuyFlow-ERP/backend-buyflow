package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByUserIdIn(Collection<Long> userIds);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UserRole userRole where userRole.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UserRole userRole where userRole.userId = :userId and userRole.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
