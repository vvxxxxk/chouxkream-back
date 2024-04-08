package com.kream.chouxkream.user;

import com.kream.chouxkream.common.model.dto.StatusCode;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }


}
