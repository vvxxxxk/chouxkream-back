package com.kream.chouxkream.auth.constants;

public abstract class AuthConst {

    public static final String ACCESS_TOKEN_TYPE = "AUTHORIZATION";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    // public static final Long ACCESS_TOKEN_EXPIRED_MS = 60000L;       // 1분
    //public static final Long ACCESS_TOKEN_EXPIRED_MS = 600000L;         // 10분
    public static final Long ACCESS_TOKEN_EXPIRED_MS = 86400000L;
    public static final Long REFRESH_TOKEN_EXPRED_MS = 86400000L;       // 24시간

}
