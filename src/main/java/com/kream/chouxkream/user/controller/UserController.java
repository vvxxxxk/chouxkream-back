package com.kream.chouxkream.user.controller;

import com.google.gson.JsonObject;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;

    public UserController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/api/login/find-email")
    public String findEmailProcess(@RequestParam("phoneNumber") String phoneNumber) {

        String findEmail = userDetailsService.findEmailProcess(phoneNumber);

        // String findEmail = userService.findEmailProcess(phoneNumber);

        JsonObject jsonObject = new JsonObject();

        if (findEmail == null) {
            jsonObject.addProperty("isSuccess", false);
            jsonObject.addProperty("email", "");
            jsonObject.addProperty("message", "회원 정보를 찾을 수 없습니다.");
        } else {
            jsonObject.addProperty("isSuccess", true);
            jsonObject.addProperty("email", findEmail);
            jsonObject.addProperty("message", "성공");
        }

        return jsonObject.toString();
    }
}
