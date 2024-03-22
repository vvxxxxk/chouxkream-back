package com.kream.chouxkream.user.model.entity;

import com.kream.chouxkream.user.model.dto.UserRoleKey;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserRole {

    @EmbeddedId
    private UserRoleKey id;

    @ManyToOne
    @MapsId("userNo")
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;
}
