package com.kream.chouxkream.auth.service;

import com.kream.chouxkream.auth.model.dto.NaverOAuth2Response;
import com.kream.chouxkream.auth.model.dto.OAuth2Response;
import com.kream.chouxkream.auth.model.dto.OAuth2UserImpl;
import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.user.model.dto.UserRoleKey;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User);

        // 플랫폼ID 확인
        // ex. 네이버, 구글 등
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverOAuth2Response(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 수 있는 아이디 값 생성
        // String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        Optional<User> optionalUser = userRepository.findByEmail(oAuth2Response.getEmail());

        // 최초 소셜 로그인 진행
        if (!optionalUser.isPresent()) {

            User user = new User();
            user.setEmail(oAuth2Response.getEmail());
            user.setUsername(oAuth2Response.getName());
            user.setPassword("password");               // 비밀번호 NOT NULL 조건에 의해 임시로 작성
            user.setPhoneNumber("010-0000-0000");       // 연락처 NOT NULL 조건에 의해 임시로 작성
            userRepository.save(user);

            Role role = new Role();
            role.setRoleId("R0003");
            role.setRoleName("ROLE_SOCIAL");

            UserRole userRole = new UserRole();
            UserRoleKey userRoleKey = new UserRoleKey();
            userRoleKey.setUserNo(userRepository.findByEmail(oAuth2Response.getEmail()).get().getUserNo());
            userRoleKey.setRoleId("R0003");

            userRole.setId(userRoleKey);
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);

            return new OAuth2UserImpl(user, role);
        }
        // 이미 소셜 로그인 정보가 등록되어 있을 때
        else {

            User user = optionalUser.get();
            user.setPassword("update_password");
            user.setPhoneNumber("010-7777-7777");

            // user 정보 업데이트
            userRepository.save(user);

            Role role = new Role();
            role.setRoleId("R0003");
            role.setRoleName("ROLE_SOCIAL");

            return new OAuth2UserImpl(user, role);
        }
    }
}
