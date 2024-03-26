package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.user.model.dto.UserDTO;
import com.kream.chouxkream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;
/*
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody UserDTO userRequest) throws Exception{
        return userService.signUp(userRequest);
    }
*/
}
