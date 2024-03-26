package com.kream.chouxkream.jwt.filter;

import com.kream.chouxkream.jwt.JwtUtils;
import com.kream.chouxkream.jwt.constants.JwtConst;
import com.kream.chouxkream.oauth2.model.dto.OAuth2UserImpl;
import com.kream.chouxkream.user.model.dto.UserDetailsImpl;
import com.kream.chouxkream.user.model.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.kream.chouxkream.jwt.constants.JwtConst.*;

public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtVerificationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JwtVerificationFilter.doFilterInternal");
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader(ACCESS_TOKEN_TYPE);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return ;
        }

        // 토큰 만료 여부 검증 (만료되어도 다음 필터로 넘기지 않음)
        try {
            jwtUtils.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access 토큰인지 확인
        String tokenType = jwtUtils.getType(accessToken);
        if (!tokenType.equals(ACCESS_TOKEN_TYPE)) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 있고, 만료시간이 지나지 않았고, access 타입이라면 아래 로직을 수행
        // 토큰에서 이메일(아이디), 권한값 획득
        String email = jwtUtils.getEmail(accessToken);
        String role_name = jwtUtils.getRole(accessToken);

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
