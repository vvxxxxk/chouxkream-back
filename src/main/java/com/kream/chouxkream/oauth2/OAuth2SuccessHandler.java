package com.kream.chouxkream.oauth2;

import com.kream.chouxkream.jwt.JwtUtils;
import com.kream.chouxkream.jwt.constants.JwtConst;
import com.kream.chouxkream.jwt.service.JwtService;
import com.kream.chouxkream.oauth2.model.dto.OAuth2UserImpl;
import com.kream.chouxkream.oauth2.service.OAuth2UserService;
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

import static com.kream.chouxkream.jwt.constants.JwtConst.*;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(JwtUtils jwtUtils, JwtService jwtService) {
        this.jwtUtils = jwtUtils;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User
        OAuth2UserImpl oAuth2User = (OAuth2UserImpl) authentication.getPrincipal();

        // get email
        String email = oAuth2User.getName();

        // get role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtils.createJwt(ACCESS_TOKEN_TYPE, email, role, ACCESS_TOKEN_EXPIRED_MS);
        String refreshToken = jwtUtils.createJwt(REFRESH_TOKEN_TYPE, email, role, REFRESH_TOKEN_EXPRED_MS);

        // refresh token Redis 저장
        jwtService.saveRefreshToken(refreshToken, email);

        response.setHeader(ACCESS_TOKEN_TYPE, accessToken);
        response.addCookie(createCookie(REFRESH_TOKEN_TYPE, refreshToken));
        response.setStatus(HttpStatus.OK.value());

        // response.addCookie(createCookie("Authorization", accessToken));
        // response.sendRedirect("http://localhost:3000/");
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
