package com.buyflow.erp.Repository;

import com.buyflow.erp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findFirstByUserNameAndEmailAndPhoneAndUseYn(String userName, String email, String phone, String useYn);

    boolean existsByLoginId(String loginId);

    List<User> findAllByOrderByCreatedAtDesc();
}
