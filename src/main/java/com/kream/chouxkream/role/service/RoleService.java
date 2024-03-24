package com.kream.chouxkream.role.service;

import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
            public String findRoleIdByRoleName(String roleName){
                Optional<Role> opRole = roleRepository.findByName(roleName);
                if(opRole.isPresent()){
                    Role role = opRole.get();
                    return role.getName();
                }else{
                    throw new RuntimeException("Role not Found for roleName: "+ roleName);
                }
            }
}
