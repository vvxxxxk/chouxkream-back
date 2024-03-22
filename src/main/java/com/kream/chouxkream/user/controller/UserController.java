package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/api/login")
//    public String loginProc(@RequestParam("email") String email,
//                            @RequestParam("password") String password) {
//
//        User user = userService.loginProc(email, password);
////        System.out.println("user.getUserDesc() = " + user.getUserDesc());
//
//
//        return "api-login";
//    }


}
