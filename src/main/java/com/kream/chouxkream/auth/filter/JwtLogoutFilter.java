package com.kream.chouxkream.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.common.model.entity.ResponseMessage;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.kream.chouxkream.auth.constants.AuthConst.*;

public class JwtLogoutFilter extends GenericFilterBean {

    private final JwtUtils jwtUtils;
    private final AuthService jwtService;

    public JwtLogoutFilter(JwtUtils jwtUtils, AuthService jwtService) {
        this.jwtUtils = jwtUtils;
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // path verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // method verify
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // get refresh token
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {

            ResponseMessage responseMessage = new ResponseMessage(400, "refresh token is null", null);

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_TYPE)) {
                refreshToken = cookie.getValue();
            }
        }

        // refresh token null 체크
        if (refreshToken == null) {

            ResponseMessage responseMessage = new ResponseMessage(400, "refresh token is null", null);

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // refresh token 만료일 체크
        try {
            jwtUtils.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            ResponseMessage responseMessage = new ResponseMessage(400, "refresh token expired", null);

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // tokenType 체크
        String tokenType = jwtUtils.getType(refreshToken);
        if (!tokenType.equals(REFRESH_TOKEN_TYPE)) {

            ResponseMessage responseMessage = new ResponseMessage(400, "invalid refresh token type", null);

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Redis(DB)에 저장된 토큰인지 체크
        Boolean isExists = jwtService.isExistRefreshToken(refreshToken);
        if (!isExists) {

            ResponseMessage responseMessage = new ResponseMessage(400, "refresh token does not exist on the server", null);

            // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
            String body = objectMapper.writeValueAsString(responseMessage);
            PrintWriter writer = response.getWriter();
            writer.print(body);

            // response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃
        // refresh token Redis(DB)에서 제거
        jwtService.deleteRefreshToken(refreshToken);

        // refresh token Cookie 값 NULL
        Cookie cookie = new Cookie(REFRESH_TOKEN_TYPE, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);

        // ResponseEntity를 이용하여 JSON 형태로 변환하여 출력
        String body = objectMapper.writeValueAsString(responseMessage);
        PrintWriter writer = response.getWriter();
        writer.print(body);

        // response status code
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
