package com.kream.chouxkream.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandleController {

//     ToDo. 컨트롤러에서만 해당 에러가 잡혀 스프링 시큐리티 필터는 추후 따로 예외 처리 필요
//    @ExceptionHandler(InternalAuthenticationServiceException.class)
//    public ResponseEntity<?> InternalAuthenticationServiceExceptionMethod(InternalAuthenticationServiceException e) {
//
//        log.error(e.getMessage());
//        e.printStackTrace();
//
//        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//    }

    // test
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> ExceptionMethod(Exception e) {

        log.error(e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
}
