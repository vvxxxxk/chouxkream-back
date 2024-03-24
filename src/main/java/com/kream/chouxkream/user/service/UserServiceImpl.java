package com.kream.chouxkream.user.service;

import com.kream.chouxkream.role.entity.UserRole;
import com.kream.chouxkream.role.repository.UserRoleRepository;
import com.kream.chouxkream.user.model.dto.UserDTO;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true) // 해당코드가 존재하지 않을경우 pw 암호화가 정상적으로 작동하지 않았음 이유가 무엇인지
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public Long signUp(UserDTO userDto) throws Exception{

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다.");
        }
        User user = userRepository.save(userDto.toEntity());
        user.encodePassword(passwordEncoder);

        /*
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(Role.USER);
        userRoleRepository.save(userRole);
        */
        return user.getUserNo();
    }
}