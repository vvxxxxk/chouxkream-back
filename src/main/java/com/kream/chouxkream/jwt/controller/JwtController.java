package com.kream.chouxkream.jwt.controller;

import com.kream.chouxkream.jwt.JwtUtils;
import com.kream.chouxkream.jwt.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.kream.chouxkream.jwt.constants.JwtConst.*;

@RestController
public class JwtController {

    private final JwtService jwtService;
    private final JwtUtils jwtUtils;

    public JwtController(JwtService jwtService, JwtUtils jwtUtils) {
        this.jwtService = jwtService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/api/jwt-reissue")
    public ResponseEntity<?> jwtReissue(HttpServletRequest request, HttpServletResponse response) {

        // get refresh token
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_TYPE)) {
                refreshToken = cookie.getValue();
            }
        }

        // refresh token null 체크
        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // refresh token 만료일 체크
        try {
            jwtUtils.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // token type 체크
        String tokenType = jwtUtils.getType(refreshToken);
        if (!tokenType.equals(REFRESH_TOKEN_TYPE)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // Redis에 저장되어 있는지 체크
        Boolean isExists = jwtService.isExistRefreshToken(refreshToken);
        if (!isExists) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // make new jwt token
        String email = jwtUtils.getEmail(refreshToken);
        String role = jwtUtils.getRole(refreshToken);

        String newAccessToken = jwtUtils.createJwt(ACCESS_TOKEN_TYPE, email, role, ACCESS_TOKEN_EXPIRED_MS);
        String newRefreshToken = jwtUtils.createJwt(REFRESH_TOKEN_TYPE, email, role, REFRESH_TOKEN_EXPRED_MS);

        /**
         * ToDo. 아래 사항 검토 필요
         *  Redis에 저장된 기존 refresh token 제거 후 새 refresh token 저장
         *   - 만료일을 지속적으로 업데이트할 수 있음 (로그인 지속시간이 길어짐?)
         */
        jwtService.deleteRefreshToken(refreshToken);
        jwtService.saveRefreshToken(newRefreshToken, email);

        // response
        response.setHeader(ACCESS_TOKEN_TYPE, newAccessToken);
        response.addCookie(createCookie(REFRESH_TOKEN_TYPE, newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
