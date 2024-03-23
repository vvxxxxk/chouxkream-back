package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.dto.UserDTO;

public interface UserService {
    public Long signUp(UserDTO userDto) throws Exception;
}
