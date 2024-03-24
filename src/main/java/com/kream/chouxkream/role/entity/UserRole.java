package com.kream.chouxkream.role.entity;

import com.kream.chouxkream.role.UserRoleId;
import com.kream.chouxkream.user.model.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity(name = "user_role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("user_no")
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne
    @MapsId("role_id")
    @JoinColumn(name = "role_id")
    private Role role;
}
