package com.kream.chouxkream.user.controller;

import com.google.gson.JsonObject;
import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.user.model.dto.UserJoinDto;
import com.kream.chouxkream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/user/join")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody UserJoinDto userJoinDto) throws Exception {
        return userService.signUp(userJoinDto);
    }

    @PostMapping("/api/login/find-email")
    public ResponseEntity<ResponseMessage> findEmailProcess(HttpServletRequest request,
                                                            @RequestParam("phoneNumber") String phoneNumber) {

        String findEmail = userService.findEmailProcess(phoneNumber);

        ResponseMessage responseMessage = new ResponseMessage();
        if (findEmail == null) {
            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(404);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("일치하는 회원 정보를 찾을 수 없습니다.");
            responseMessage.addData("email", null);
        } else {
            responseMessage.setIsSuccess(true);
            responseMessage.setStatusCode(200);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("성공");
            responseMessage.addData("email", findEmail);
        }

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);

    }
}
