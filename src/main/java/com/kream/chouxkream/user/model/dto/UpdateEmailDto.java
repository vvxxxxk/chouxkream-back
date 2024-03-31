package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateEmailDto {

    @Email
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;
}
