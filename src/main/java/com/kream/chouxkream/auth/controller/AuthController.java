package com.kream.chouxkream.auth.controller;

import com.kream.chouxkream.auth.model.dto.AuthDTO;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.user.model.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody AuthDTO authRequest) throws Exception{
        return authService.signUp(authRequest);
    }
}
