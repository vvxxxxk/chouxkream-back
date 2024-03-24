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
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    private Role role;;
}
