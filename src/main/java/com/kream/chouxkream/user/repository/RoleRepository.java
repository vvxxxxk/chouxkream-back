package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.user.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
