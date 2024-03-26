package com.kream.chouxkream.auth.filter;

import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.model.dto.UserDetailsImpl;
import com.kream.chouxkream.role.entity.Role;
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

import static com.kream.chouxkream.auth.constants.AuthConst.*;

public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtil;

    public JWTFilter(JwtUtils jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JWTFilter.doFilterInternal");
        System.out.println("JWTFilter.doFilterInternal");
        System.out.println("JWTFilter.doFilterInternal");

        String accessToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {

            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("cookies = " + cookies);
        for (Cookie cookie : cookies) {
            System.out.println("cookie.getValue() = " + cookie.getValue());
            if (cookie.getName().equals(ACCESS_TOKEN_TYPE)) {

                accessToken = cookie.getValue();
            }
        }

        System.out.println("accessToken = " + accessToken);

        //Authorization 헤더 검증
        if (accessToken == null) {

            System.out.println("access token null");
            filterChain.doFilter(request, response);
            return;
        }


        if (jwtUtil.isExpired(accessToken)) {

            System.out.println("access token expired");
            filterChain.doFilter(request, response);
            return;
        }

        //토큰에서 username과 role 획득
        String email = jwtUtil.getEmail(accessToken);
        String role_name = jwtUtil.getRole(accessToken);

        User user = new User();
        Role role = new Role();
        user.setEmail(email);
        role.setRoleName(role_name);

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user, role);
        //OAuth2UserImpl oAuth2User = new OAuth2UserImpl(user, role);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
        //Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
