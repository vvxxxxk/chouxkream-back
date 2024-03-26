package com.kream.chouxkream.auth;

import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.auth.model.dto.OAuth2UserImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static com.kream.chouxkream.auth.constants.AuthConst.*;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final AuthService jwtService;

    public OAuth2SuccessHandler(JwtUtils jwtUtils, AuthService jwtService) {
        this.jwtUtils = jwtUtils;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // get oAuth2User
        OAuth2UserImpl oAuth2User = (OAuth2UserImpl) authentication.getPrincipal();

        // get email
        String email = oAuth2User.getName();

        // get role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // String accessToken = jwtUtils.createJwt(ACCESS_TOKEN_TYPE, email, role, ACCESS_TOKEN_EXPIRED_MS);
        String refreshToken = jwtUtils.createJwt(REFRESH_TOKEN_TYPE, email, role, REFRESH_TOKEN_EXPRED_MS);

        // refresh token Redis 저장
        jwtService.saveRefreshToken(refreshToken, email);

        // create cookie (access, refresh)
        //response.addCookie(createCookie(ACCESS_TOKEN_TYPE, accessToken));
        response.addCookie(createCookie(REFRESH_TOKEN_TYPE, refreshToken));

        // response
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);  // https 통신 진행 시 설정
        cookie.setPath("/");     // 쿠키 적용 범위 설정
        cookie.setHttpOnly(true);

        return cookie;
    }
}
