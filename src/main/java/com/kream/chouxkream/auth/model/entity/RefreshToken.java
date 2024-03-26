package com.kream.chouxkream.auth.model.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 24*60*60)
public class RefreshToken {

    public RefreshToken(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }

    // java.persistence.Id가 아닌 org.springframework.data.annotation.Id를 import
    // JPA 의존성이 필요없기 때문이며 persistence로 import 시 에러 발생
    @Id
    private String refreshToken;
    private String email;
}
