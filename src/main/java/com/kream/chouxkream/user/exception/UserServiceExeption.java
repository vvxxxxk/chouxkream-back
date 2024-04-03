package com.kream.chouxkream.user.exception;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import lombok.Getter;

@Getter
public class UserServiceExeption extends RuntimeException {

    private final ResponseMessageDto responseMessageDto;

    public UserServiceExeption(ResponseMessageDto responseMessageDto) {
        this.responseMessageDto = responseMessageDto;
    }
}
