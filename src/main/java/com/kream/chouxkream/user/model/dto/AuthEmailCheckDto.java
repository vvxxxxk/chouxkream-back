package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AuthEmailCheckDto {

    @Email
    private String email;

    @Pattern(regexp = "\\d{6}", message = "6자리 숫자를 입력해주세요.")
    private String authNumber;
}
