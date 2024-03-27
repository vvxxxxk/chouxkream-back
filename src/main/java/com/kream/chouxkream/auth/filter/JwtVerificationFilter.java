package com.kream.chouxkream.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.model.dto.OAuth2UserImpl;
import com.kream.chouxkream.auth.model.dto.UserDetailsImpl;
import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static com.kream.chouxkream.auth.constants.AuthConst.*;

public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtVerificationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader(ACCESS_TOKEN_TYPE);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null ) {
            filterChain.doFilter(request, response);
            return ;
        }

        ResponseMessage responseMessage = new ResponseMessage();
        ObjectMapper objectMapper = new ObjectMapper();


        // 토큰 만료 여부 검증 (만료되어도 다음 필터로 넘기지 않음)
        try {
            jwtUtils.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("access token expired");

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);

            // response body
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (MalformedJwtException e) {

            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("unable to parse the access token");

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);

            // response body
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access 토큰인지 확인
        String tokenType = jwtUtils.getType(accessToken);
        if (!tokenType.equals(ACCESS_TOKEN_TYPE)) {

            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("invalid access token type");

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);

            // response body
            PrintWriter writer = response.getWriter();
            writer.print(body);

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

        if (role_name.equals("ROLE_USER") || role_name.equals("ROLE_ADMIN")) {

            UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user, role);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } else if (role_name.equals("ROLE_SOCIAL")) {

            OAuth2UserImpl oAuth2User = new OAuth2UserImpl(user, role);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } else {

            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("invalid roleName");

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);

            // response body
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }
}
