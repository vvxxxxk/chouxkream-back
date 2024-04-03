package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.user.model.dto.*;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<ResponseMessageDto> join(@Valid @RequestBody UserJoinDto userJoinDto) {

        // 이메일 인증 여부 체크
        if (!userJoinDto.isEmailAuth()) {

            StatusCode statusCode = StatusCode.MISSING_REQUIRED_FIELD;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 이메일 중복 체크
        boolean isEmailDuplicate = userService.isEmailExists(userJoinDto.getEmail());
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 회원가입
        userService.join(userJoinDto);

        StatusCode statusCode = StatusCode.JOIN_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 메일 발송")
    @PostMapping("/auth-email")
    public ResponseEntity<ResponseMessageDto> sendAuthEmail(@Valid @RequestBody EmailDto emailDto) throws MessagingException {


        userService.sendAuthEmail(emailDto.getEmail());

        StatusCode statusCode = StatusCode.AUTH_EMAIL_SEND_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 번호 체크")
    @PostMapping("/auth-number")
    public ResponseEntity<ResponseMessageDto> checkAuthNumber(@Valid @RequestBody AuthEmailCheckDto authEmailCheckDto) {

        Boolean isAuthEmailCheck = userService.checkAuthNumber(authEmailCheckDto.getEmail(), authEmailCheckDto.getAuthNumber());

        if (isAuthEmailCheck) {

            StatusCode statusCode = StatusCode.AUTH_EMAIL_CHECK_SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.AUTH_EMAIL_CHECK_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "이메일 찾기")
    @GetMapping("/email")
    public ResponseEntity<ResponseMessageDto> findEmailByPhoneNumber(@Valid @RequestBody PhoneNumberDto phoneNumberDto) {

        Optional<User> optionalUser = userService.findByPhoneNumber(phoneNumberDto.getPhoneNumber());

        if (optionalUser.isPresent()) {

            StatusCode statusCode = StatusCode.FIND_EAMIL_SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            String maskingEmail = emailMasking(optionalUser.get().getEmail());
            responseMessageDto.addData("email", maskingEmail);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.FIND_EAMIL_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    private String emailMasking(String email) {

        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹
        return id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);
    }

    @ApiOperation(value = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ResponseMessageDto> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {

            UserInfoDto userInfoDto = new UserInfoDto(optionalUser.get());

            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            responseMessageDto.addData("user", userInfoDto);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "이메일 변경")
    @PutMapping("/me/email")
    public ResponseEntity<ResponseMessageDto> updateEmail(@Valid @RequestBody EmailDto updateEmailDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String updateEmail = updateEmailDto.getEmail();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 변경 후 이메일 중복 검증
        boolean isEmailDuplicate = userService.isEmailExists(updateEmail);
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        userService.updateEmail(email, updateEmail);

        StatusCode statusCode = StatusCode.USER_INFO_UPDATE_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateEmail", updateEmail);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "이름 변경")
    @PutMapping("/me/name")
    public ResponseEntity<ResponseMessageDto> updateName(@Valid @RequestBody UsernameDto usernameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateName = usernameDto.getUsername();
        userService.updateName(email, updateName);

        StatusCode statusCode = StatusCode.USER_INFO_UPDATE_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateName", updateName);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "닉네임 변경")
    @PutMapping("/me/nickname")
    public ResponseEntity<ResponseMessageDto> updateNickname(@Valid @RequestBody NicknameDto nicknameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateNickname = nicknameDto.getNickname();
        userService.updateNickname(email, updateNickname);

        StatusCode statusCode = StatusCode.USER_INFO_UPDATE_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateNickname", updateNickname);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "소개글 변경")
    @PutMapping("/me/user-desc")
    public ResponseEntity<ResponseMessageDto> updateUserDesc(@Valid @RequestBody UserDescDto userDescDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateUserDesc = userDescDto.getUserDesc();
        userService.updateUserDesc(email, updateUserDesc);

        StatusCode statusCode = StatusCode.USER_INFO_UPDATE_SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateUserDesc", updateUserDesc);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }
}
