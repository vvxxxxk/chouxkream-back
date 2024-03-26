package com.kream.chouxkream.jwt.filter;

import com.kream.chouxkream.jwt.JwtUtils;
import com.kream.chouxkream.oauth2.model.dto.OAuth2UserImpl;
import com.kream.chouxkream.user.model.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtil;

    public JWTFilter(JwtUtils jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Authorization")) {

                authorization = cookie.getValue();
            }
        }

        //Authorization 헤더 검증
        if (authorization == null) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰
        String token = authorization;

        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰에서 username과 role 획득
        String email = jwtUtil.getEmail(token);
        String role_name = jwtUtil.getRole(token);

        User user = new User();
        Role role = new Role();
        user.setEmail(email);
        role.setRoleName(role_name);

        // UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user, role);
        OAuth2UserImpl oAuth2User = new OAuth2UserImpl(user, role);

        // 스프링 시큐리티 인증 토큰 생성
        //Authentication authToken = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
