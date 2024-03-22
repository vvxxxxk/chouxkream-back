package com.kream.chouxkream.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/all")
    public String allPage() {
        return "모든 사용자 접근 가능 페이지";
    }

    @GetMapping("/user")
    public String userPage() {
        return "회원 접근 가능 페이지";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "관리자 접근 가능 페이지";
    }


}
