package com.kream.chouxkream.user.model.entity;

import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.user.model.dto.UserRoleKey;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
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
