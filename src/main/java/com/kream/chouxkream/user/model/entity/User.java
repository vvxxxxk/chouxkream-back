package com.kream.chouxkream.user.model.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(nullable = false, unique = true, length = 20)
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

    @Column(columnDefinition = "int default 0")
    private int point;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private Timestamp createDate;

    @Column(columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private Timestamp updateDate;

    @Column(columnDefinition = "bit default 0")
    private boolean isActive;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlist;

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    public void deActivate() {
        this.isActive = false;
    }

    public void Activate() {
        this.isActive = true;
    }
}