package com.kream.chouxkream.user.controller;

import com.google.gson.JsonObject;
import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.user.model.dto.UserInfoDto;
import com.kream.chouxkream.user.model.dto.UserJoinDto;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody UserJoinDto userJoinDto) throws Exception {
        return userService.signUp(userJoinDto);
    }

    @GetMapping("/email")
    public ResponseEntity<ResponseMessage> findEmailByPhoneNumber(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber) {

        String findEmail = userService.findEmailByPhoneNumber(phoneNumber);

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


    @GetMapping("/me")
    public ResponseEntity<ResponseMessage> getUserInfo(HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserInfo(email);

        UserInfoDto userInfoDto = new UserInfoDto(user);

        ResponseMessage responseMessage = new ResponseMessage();
        if (user == null) {
            responseMessage.setIsSuccess(false);
            responseMessage.setStatusCode(404);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("일치하는 회원 정보를 찾을 수 없습니다.");
            responseMessage.addData("user", null);
        } else {
            responseMessage.setIsSuccess(true);
            responseMessage.setStatusCode(200);
            responseMessage.setMethod(request.getMethod());
            responseMessage.setUri(request.getRequestURI());
            responseMessage.setMessage("");
            responseMessage.addData("user", userInfoDto);
        }


        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }
}
