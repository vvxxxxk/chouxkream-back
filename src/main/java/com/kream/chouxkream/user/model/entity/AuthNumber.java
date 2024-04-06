package com.kream.chouxkream.user.model.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "authNumber", timeToLive = 60*5)
public class AuthNumber {

    public AuthNumber(String email, String authNum) {
        this.email = email;
        this.authNum = authNum;
    }

    @Id
    private String email;
    private String authNum;
}