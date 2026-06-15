package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

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
            where (:keyword is null
                   or lower(user.loginId) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.userName) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.email) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.phone) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.departmentName) like lower(concat(concat('%', :keyword), '%'))
                   or lower(user.positionName) like lower(concat(concat('%', :keyword), '%')))
              and (:status is null or user.status = :status)
              and (:useYn is null or user.useYn = :useYn)
              and (:jobRank is null or user.jobRank = :jobRank)
            """)
    Page<User> search(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("useYn") String useYn,
            @Param("jobRank") String jobRank,
            Pageable pageable
    );
}
