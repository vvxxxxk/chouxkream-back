package com.kream.chouxkream.common.model.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessageDto {

    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
