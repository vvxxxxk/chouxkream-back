package com.kream.chouxkream.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import static com.kream.chouxkream.auth.constants.AuthConst.*;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.authService = authService;

        // default url, username 파라미터 변경
        setFilterProcessesUrl("/api/login");
        setUsernameParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 요청 정보에서 아이디, 비밀번호 추출
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        // 스프링 시큐리티에서 username, password 검증하려면 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        // 검증을 위해 token을 AuthenticationManager로 전달
        try {
            return authenticationManager.authenticate(authToken);
        } catch (InternalAuthenticationServiceException e) {
            // 내부 인증 서비스 예외 발생 처리
            log.error("내부 인증 서비스 예외 발생: {}", e.getMessage());
            throw new AuthenticationServiceException("Internal authentication service exception", e);
        }
    }

    // 로그인 성공 - JWT token 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 아이디
        String email = authResult.getName();
        // 권한
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // access token 생성, 10분
        String accessToken = jwtUtils.createJwt(ACCESS_TOKEN_TYPE, email, role, ACCESS_TOKEN_EXPIRED_MS);
        // refresh token 생성, 24시간
        String refreshToken = jwtUtils.createJwt(REFRESH_TOKEN_TYPE, email, role, REFRESH_TOKEN_EXPRED_MS);

        // refresh token Redis 저장
        authService.saveRefreshToken(refreshToken, email);

        // response
        response.setHeader(ACCESS_TOKEN_TYPE, accessToken);
        response.addCookie(createCookie(REFRESH_TOKEN_TYPE, refreshToken));

        ObjectMapper objectMapper = new ObjectMapper();
        StatusCode statusCode = StatusCode.LOGIN_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);

        // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
        String body = objectMapper.writeValueAsString(responseMessageDto);
        PrintWriter writer = response.getWriter();
        writer.print(body);

        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // ToDo. 스프링 시큐리티에서 한글 깨지는 현상 원인 추정, https://green-bin.tistory.com/119
        ObjectMapper objectMapper = new ObjectMapper();
        StatusCode statusCode = StatusCode.LOGIN_FAILED;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);

        // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
        String body = objectMapper.writeValueAsString(responseMessageDto);

        // response body
        PrintWriter writer = response.getWriter();
        writer.print(body);

        // response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
