package com.kream.chouxkream.common.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandleController extends ResponseEntityExceptionHandler {

    // example

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exampleExceptionMethod(Exception e) {

        log.error(e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //email 형식에 대한 오류
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> emailFormExceptionMethod(ConstraintViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("is_success", false);
        response.put("error", "이메일 양식에 맞게 입력 해주십시오.");
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    //email 중복에 대한 오류
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> emailDuplicationExceptionMethod(DataIntegrityViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("is_success", false);
        response.put("error", "중복된 이메일 입니다.");
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    //pw 형식에 대한 오류
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("is_success", false);
        response.put("error", "패스워드의 형식을 맞춰 주십시오.");
        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
