package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UsernameDto {

    @Size(min=1, max=10, message = "이름은 2-10글자 사이어야 합니다.")
    private String username;
}
