package com.kream.chouxkream.common.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResponseMessage {

    private boolean isSuccess;
    private int statusCode;
    private String method;
    private String uri;
    private String message;
    private Map<String, Object> data;

    public ResponseMessage() {
        this.data = new HashMap<>();
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
}
