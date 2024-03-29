package com.kream.chouxkream.common.controller;

import com.kream.chouxkream.common.model.entity.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandleController {

    // example
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exampleExceptionMethod(Exception e) {
        /*
            ...
         */
        log.error(e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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

}
