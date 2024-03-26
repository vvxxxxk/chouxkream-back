package com.kream.chouxkream.role.repository;

import com.kream.chouxkream.role.UserRoleId;
import com.kream.chouxkream.role.entity.UserRole;
import com.kream.chouxkream.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
