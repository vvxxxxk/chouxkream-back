package com.kream.chouxkream.role.entity;

import com.kream.chouxkream.user.model.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter     // ToDo. @Setter 추후 지우는 것 고려
public class Role {
    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name="role_name", nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserRole> userRoles = new HashSet<>();
}

