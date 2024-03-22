package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.model.entity.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {

    List<UserRole> findByIdUserNo(Long userNo);
}
