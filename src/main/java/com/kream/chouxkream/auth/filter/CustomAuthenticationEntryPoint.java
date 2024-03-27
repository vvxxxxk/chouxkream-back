package com.kream.chouxkream.auth.filter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 권한이 필요한 페이지에 접근 시 로그인 페이지로 리다이렉트
        // ToDo. 해당 필터를 백엔드에서 처리해주어야 하는지 여부는 검토가 필요
        response.sendRedirect("http://localhost:3000/");
    }
}
