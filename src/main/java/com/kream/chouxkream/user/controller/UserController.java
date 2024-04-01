package com.kream.chouxkream.user.controller;

import com.google.gson.JsonObject;
import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.user.model.dto.*;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

import static com.kream.chouxkream.auth.constants.AuthConst.REFRESH_TOKEN_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<ResponseMessage> join(@Valid @RequestBody UserJoinDto userJoinDto) {

        userService.join(userJoinDto);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 인증 메일 발송
     */
    @PostMapping("/auth-email")
    public ResponseEntity<ResponseMessage> sendAuthEmail(@RequestParam("email") String email) {

        userService.sendAuthEmail(email);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 인증 번호 체크
     */
    @PostMapping("/auth-number")
    public ResponseEntity<ResponseMessage> checkAuthNumber(@RequestParam("email") String email,
                                                           @RequestParam("authNumber") String authNumber) {

        Boolean isCheck = userService.checkAuthNumber(email, authNumber);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 이메일 찾기
     */
    @GetMapping("/email")
    public ResponseEntity<ResponseMessage> findEmailByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {

        String findEmail = userService.findEmailByPhoneNumber(phoneNumber);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("email", findEmail);

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 회원 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseMessage> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserInfo(email);
        UserInfoDto userInfoDto = new UserInfoDto(user);

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("user", userInfoDto);

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 이메일 변경
     */
    @PutMapping("/me/email")
    public ResponseEntity<ResponseMessage> updateEmail(@Valid @RequestBody UpdateEmailDto updateEmailDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updateEmail(email, updateEmailDto.getEmail());

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("updateEmail", updateEmailDto.getEmail());

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 이름 변경
     */
    @PutMapping("/me/name")
    public ResponseEntity<ResponseMessage> updateName(@Valid @RequestBody UpdateNameDto updateNameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updateName(email, updateNameDto.getUsername());

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("updateName", updateNameDto.getUsername());

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 닉네임 변경
     */
    @PutMapping("/me/nickname")
    public ResponseEntity<ResponseMessage> updateNickname(@Valid @RequestBody UpdateNicknameDto updateNicknameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updateNickname(email, updateNicknameDto.getNickname());

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("updateNickname", updateNicknameDto.getNickname());

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    /**
     * 소개글 변경
     */
    @PutMapping("/me/user-desc")
    public ResponseEntity<ResponseMessage> updateUserDesc(@Valid @RequestBody UpdateUserDescDto updateUserDescDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updateUserDesc(email, updateUserDescDto.getUserDesc());

        ResponseMessage responseMessage = new ResponseMessage(200, "", null);
        responseMessage.addData("updateUserDesc", updateUserDescDto.getUserDesc());

        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }
}
