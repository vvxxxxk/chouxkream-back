package com.kream.chouxkream.common.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuth2TestController {

    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {
        System.out.println("AuthTestController.myAPI");
        return "my route";
    }
}
