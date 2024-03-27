package com.kream.chouxkream.user.service;

import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.repository.RoleRepository;
import com.kream.chouxkream.user.model.dto.UserJoinDto;
import com.kream.chouxkream.user.model.dto.UserRoleKey;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 해당코드가 존재하지 않을경우 pw 암호화가 정상적으로 작동하지 않았음 이유가 무엇인지
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(UserJoinDto userJoinDto) throws Exception {
        User user = userRepository.save(userJoinDto.toEntity());
        user.encodePassword(passwordEncoder);

        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not Found"));

        UserRoleKey userRoleKey = new UserRoleKey(user.getUserNo(), role.getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(userRoleKey);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return user.getUserNo();
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
