package com.kream.chouxkream.user.model.entity;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    // 회원가입에서 요구되는 정보 : email, password, phonenumber
    @Id
    @Column(name = "user_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(length = 20, name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String userDesc;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int point;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateDate;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isActive;

    /*
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role role;
    */
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
}