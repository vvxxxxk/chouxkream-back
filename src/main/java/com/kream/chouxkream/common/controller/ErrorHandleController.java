package com.kream.chouxkream.common.controller;

import com.kream.chouxkream.common.model.dto.ErrorMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandleController {

    private final HttpHeaders headers = new HttpHeaders();


    @PostConstruct
    public void init() {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    // ########################################################################
    // # 4xx
    // ########################################################################

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> exceptionHandler(MessagingException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Failed to connect to the email server, invalid email address used, or authentication error occurred.")
                .build(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> exceptionHandler(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Data integrity constraints have been violated")
                .build(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<?> exceptionHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Required request body is missing")
                .build(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> exceptionHandler(IllegalArgumentException e) {
        log.error(e.getMessage()+"fd");
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> exceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 에러 메시지를 담을 Map 생성
        Map<String, String> errors = new HashMap<>();

        // 모든 필드 에러를 순회하며 메시지를 Map에 추가
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // 사용자 정의 에러 메시지 객체에 에러 메시지 Map을 포함시켜 반환
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(errors.toString())
                .build(), headers, HttpStatus.BAD_REQUEST);
    }


    // ########################################################################
    // # 5xx
    // ########################################################################
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<?> exceptionHandler(RuntimeException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("INTERNAL_SERVER_ERROR")
                .build(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> exceptionHandler(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorMessageDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("INTERNAL_SERVER_ERROR")
                .build(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
