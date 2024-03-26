package com.kream.chouxkream.role.controller;

import com.kream.chouxkream.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/role")
@RestController
public class RoleController {

    RoleService roleService;

}
