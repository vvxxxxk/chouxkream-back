package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PhoneNumberAndEmailDto {

    @Pattern(regexp = "^01(?:0|1|[6-9])-(\\d{3,4})-(\\d{4})$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phoneNumber;

    @Email
    private String email;
}
