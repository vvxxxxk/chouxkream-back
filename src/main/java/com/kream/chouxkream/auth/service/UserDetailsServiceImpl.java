package com.kream.chouxkream.auth.service;

import com.kream.chouxkream.auth.model.dto.UserDetailsImpl;
import com.kream.chouxkream.user.model.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.RoleRepository;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
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
