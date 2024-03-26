package com.kream.chouxkream.role.controller;

import com.kream.chouxkream.role.service.RoleService;
import com.kream.chouxkream.user.model.dto.UserDTO;
import com.kream.chouxkream.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/role")
@RestController
public class RoleController {
    RoleService roleService;
   /*
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> giveAdminRole(@Valid @RequestBody User user) throws Exception{

    }*/
}
