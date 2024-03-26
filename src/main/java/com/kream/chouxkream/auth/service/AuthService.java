package com.kream.chouxkream.auth.service;

import com.kream.chouxkream.auth.model.entity.RefreshToken;
import com.kream.chouxkream.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true) // 해당코드가 존재하지 않을경우 pw 암호화가 정상적으로 작동하지 않았음 이유가 무엇인지
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository jwtRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;


    @Override
    @Transactional
    public Long signUp(AuthDTO authDTO) throws Exception{

        if (userRepository.findByEmail(authDTO.getEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다.");
        }
        User user = userRepository.save(authDTO.toEntity());
        user.encodePassword(passwordEncoder);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not Found"));

        UserRoleId userRoleId = new UserRoleId(user.getUserNo(), role.getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return user.getUserNo();
    }

    @Transactional
    public void saveRefreshToken(String refreshToken, String email) {
        jwtRepository.save(new RefreshToken(refreshToken, email));
    }

    @Transactional
    public Boolean isExistRefreshToken(String refreshToken) {
        return jwtRepository.existsById(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        jwtRepository.deleteById(refreshToken);
    }
}
