package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDescDto {

    @Size(min=0, max=255, message = "소개글은 255자 이하로 작성 가능합니다.")
    private String userDesc;
}
