package com.kream.chouxkream.user.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@DynamicInsert
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String userDesc;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int point;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateDate;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isActive;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<UserRole> userRoles = new HashSet<>();
}