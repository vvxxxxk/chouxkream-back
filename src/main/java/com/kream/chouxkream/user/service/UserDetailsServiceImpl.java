package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.model.dto.UserDetailsImpl;
import com.kream.chouxkream.user.model.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.RoleRepository;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if (user != null) {
            List<UserRole> userRoles = userRoleRepository.findByIdUserNo(user.getUserNo());
            if (!userRoles.isEmpty()) {
                Role role = roleRepository.findById(userRoles.get(0).getRole().getRoleId()).get();
                if (role != null) {
                    return new UserDetailsImpl(user, role);
                }
            }
        }

        return null;
    }
}
