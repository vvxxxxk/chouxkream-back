package com.kream.chouxkream.jwt.constants;

public abstract class JwtConst {

    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    public static final Long ACCESS_TOKEN_EXPIRED_MS = 600000L;     // 10분
    public static final Long REFRESH_TOKEN_EXPRED_MS = 86400000L;   // 24시간
}
