package com.kream.chouxkream.role.repository;

import com.kream.chouxkream.role.UserRoleId;
import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}