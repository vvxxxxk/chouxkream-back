package com.kream.chouxkream.user.model.dto;

import com.kream.chouxkream.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoDto {

    public UserInfoDto(User user) {
        this.userNo = user.getUserNo();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.point = user.getPoint();
    }

    private Long userNo;

    private String email;

    private String username;

    private String nickname;

    private String userDesc;

    private String phoneNumber;

    private int point;
}
