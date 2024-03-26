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
import java.util.Optional;

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

    public String findEmailProcess(String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            String maskingEmail = emailMasking(optionalUser.get().getEmail());
            return maskingEmail;
        }

        return null;
    }

    private String emailMasking(String email) {

        // 이메일에서 아이디 추출
        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹
        String maskingResult = id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);

        return maskingResult;
    }
}
