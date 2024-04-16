package com.kream.chouxkream.common.model.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseMessageDto {

    public ResponseMessageDto(Integer code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = new HashMap<>();
    }

    private Integer code;
    private String message;
    private Map<String, Object> data;

    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
