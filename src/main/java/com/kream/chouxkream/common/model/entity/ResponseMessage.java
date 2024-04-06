package com.kream.chouxkream.common.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResponseMessage {

    public ResponseMessage(int statusCode, String message, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new HashMap<>();
    }

    public ResponseMessage() {
        this.data = new HashMap<>();
    }

    private int statusCode;
    private String message;
    private Map<String, Object> data;



    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
