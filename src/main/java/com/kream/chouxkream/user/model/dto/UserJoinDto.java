package com.kream.chouxkream.user.model.dto;

import com.kream.chouxkream.user.model.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserJoinDto {

    @NotBlank(message = "email 입력 필수!")
    private String email;

    @NotBlank(message = "pw 입력 필수!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    @NotBlank(message = "전화번호 입력 필수!")
    private String phoneNumber;

    @Builder
    public User toEntity(){
        return User.builder()
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .build();
    }
}
