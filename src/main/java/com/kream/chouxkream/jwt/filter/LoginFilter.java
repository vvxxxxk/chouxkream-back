package com.kream.chouxkream.jwt.filter;

import com.kream.chouxkream.jwt.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;

        // UsernamePasswordAuthenticationFilter의 default processing url("/login")을 "/api/login"으로 변경
        setFilterProcessesUrl("/api/login");
        setUsernameParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 요청 정보에서 아이디, 비밀번호 추출
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("email = " + email);
        System.out.println("password = " + password);

        // 스프링 시큐리티에서 username, password 검증하려면 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        // 검증을 위해 token을 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공 - JWT token 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 아이디
        String email = authResult.getName();
        System.out.println("email = " + email);
        // 권한
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        System.out.println("role = " + role);

        // access token 생성, 10분
        String accessToken = jwtUtils.createJwt("access", email, role, 600000L);
        // refresh token 생성, 24시간
        String refreshToken = jwtUtils.createJwt("refresh", email, role, 86400000L);

        // response
        response.setHeader("accessToken", accessToken);
        response.addCookie(createCookie("refreshToken", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // response
        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);  // https 통신 진행 시 설정
        // cookie.setPath("/");     // 쿠키 적용 범위 설정
        cookie.setHttpOnly(true);

        return cookie;
    }
}
