package com.buyflow.erp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByLoginId(String loginId);
}
