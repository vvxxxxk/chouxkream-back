package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateNicknameDto {

    @Size(min=1, max=50, message = "이름은 50글자 이하로 지어야 합니다.")
    private String nickname;
}
