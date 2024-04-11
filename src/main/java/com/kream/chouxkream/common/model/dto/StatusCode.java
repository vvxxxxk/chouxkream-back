package com.kream.chouxkream.common.model.dto;

import lombok.Getter;

@Getter
public enum StatusCode {

    // 성공 코드
    SUCCESS(2000, "성공"),

    // 상품 관련 성공 코드
    PRODUCT_CREATION_SUCCESS(2001, "상품 생성 성공"),
    PRODUCT_DELETION_SUCCESS(2002, "상품 삭제 성공"),

    // 사용자 관련 성공 코드
    LOGIN_SUCCESS(2003, "로그인 성공"),
    USER_INFO_UPDATE_SUCCESS(2004, "회원정보 수정 성공"),
    JOIN_SUCCESS(2009, "회원가입 성공"),
    LOGOUT_SUCCESS(2010, "로그아웃 성공"),
    AUTH_EMAIL_SEND_SUCCESS(2011, "인증메일 발송 성공"),
    AUTH_EMAIL_CHECK_SUCCESS(2012, "인증메일 인증 성공"),
    FIND_EAMIL_SUCCESS(2013, "이메일 찾기 성공"),
    FIND_USER_SUCCESS(2014, "사용자 조회 성공"),

    // 결제 관련 성공코드
    PAYMENT_SUCCESS(2005, "결제 성공"),

    // 상품 관련 에러 코드
    PRODUCT_NOT_FOUND(4001, "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(4002, "재고가 부족합니다."),

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

    // 파일 관련 상태코드
    FILE_UPLOAD_SUCCESS(2006, "파일 업로드 성공"),
    FILE_UPLOAD_FAILURE(4008, "파일 업로드 실패"),

    // 권한 관련 상태코드
    PERMISSION_DENIED(4009, "권한이 없습니다."),

    // jwt 관련 성공 코드
    JWT_TOKEN_CREATED(2007, "JWT 토큰이 성공적으로 생성되었습니다."),
    JWT_TOKEN_VERIFIED(2008, "JWT 토큰이 성공적으로 검증되었습니다."),

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
