package com.kream.chouxkream.auth.service;

import com.kream.chouxkream.auth.model.dto.AuthDTO;

public interface AuthService {
    public Long signUp(AuthDTO authDTO) throws Exception;
}
