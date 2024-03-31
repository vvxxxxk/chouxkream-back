package com.kream.chouxkream.user.exception;

import com.kream.chouxkream.common.model.entity.ResponseMessage;
import lombok.Getter;

@Getter
public class UserServiceExeption extends RuntimeException {

    private final ResponseMessage responseMessage;

    public UserServiceExeption(ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }
}
