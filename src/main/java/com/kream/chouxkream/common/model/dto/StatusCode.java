package com.kream.chouxkream.common.model.dto;

import lombok.Getter;

@Getter
public enum StatusCode {

    // 성공 코드
    SUCCESS(2000, "성공"),

    // 상품 관련 에러 코드
    PRODUCT_NOT_FOUND(4001, "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(4002, "재고가 부족합니다."),

    // 검색 관련 에러코드
    PAGEINFO_NOT_EXIST(4018,"검색 페이징 정보가 없습니다."),

    // 사용자 관련 에러 코드
    LOGIN_FAILED(4003, "로그인에 실패 하였습니다."),
    AUTH_EMAIL_CHECK_FAILED(4014, "인증메일 인증 실패 하였습니다."),
    FIND_EAMIL_FAILED(4015, "이메일 찾기 실패 하였습니다."),
    FIND_USER_FAILED(4016, "일치하는 사용자 정보를 찾을 수 없습니다."),
    USER_INFO_UPDATE_FAILED(4017, "사용자 정보 변경 실패"),

    // 결제 관련 에러 코드
    PAYMENT_REJECTED(4004, "결제가 거부되었습니다."),
    INVALID_PAYMENT_METHOD(4005, "유효하지 않은 결제 수단입니다."),

    // 네트워크 및 서버 관련 에러 코드
    SERVER_NOT_RESPONDING(5001, "서버가 응답하지 않습니다."),
    NETWORK_DISCONNECTED(5002, "네트워크 연결이 끊겼습니다."),

    // 입력 유효성 검사 에러 코드
    INVALID_EMAIL_FORMAT(4006, "유효하지 않은 이메일 형식입니다."),
    MISSING_REQUIRED_FIELD(4007, "필수 입력 필드가 누락되었습니다."),

    // 권한 관련 상태코드
    PERMISSION_DENIED(4009, "권한이 없습니다."),

    // jwt 관련 실패 코드
    JWT_TOKEN_EXPIRED(4010, "JWT 토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN(4011, "JWT 토큰이 유효하지 않습니다."),
    JWT_TOKEN_NOT_FOUND(4012, "JWT 토큰이 존재하지 않습니다."),
    OTHER_JWT_ERROR(4999, "기타 JWT 토큰 관련 오류가 발생했습니다."),

    // 데이터 베이스 관련 에러코드
    DATABASE_CONNECTION_ERROR(5003, "데이터베이스 연결 오류"),
    DATABASE_QUERY_ERROR(5004, "데이터베이스 쿼리 오류"),

    // 이미 존재하는 리소스 관련 에러코드
    RESOURCE_ALREADY_EXISTS(4013, "해당 리소스는 이미 존재합니다.");



    private final Integer code;
    private final String message;

    StatusCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
