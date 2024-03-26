package com.kream.chouxkream.role.service;

import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.user.model.dto.UserRoleKey;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.role.repository.RoleRepository;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

        public String findRoleIdByRoleName(String roleName){
            Optional<Role> opRole = roleRepository.findByRoleName(roleName);
            if(opRole.isPresent()){
                Role role = opRole.get();
                return role.getRoleName();
            }else{
                throw new RuntimeException("Role not Found for roleName: "+ roleName);
            }
        }

        public void giveAdminRole(User user){
            Role role = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Default role not Found"));
            UserRoleKey userRoleKey = new UserRoleKey(user.getUserNo(), role.getRoleId());
            UserRole userRole = new UserRole();
            userRole.setId(userRoleKey);
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
}
