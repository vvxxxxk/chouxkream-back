package com.kream.chouxkream.user.entity;

import com.kream.chouxkream.role.Role;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, name = "email")
    private String email;

    @Column(name = "pw")
    private String pw;


    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role role;

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.pw = passwordEncoder.encode(pw);
    }
}