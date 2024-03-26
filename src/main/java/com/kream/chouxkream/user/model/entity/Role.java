package com.kream.chouxkream.user.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role {

    public Role() {
    }

    @Id
    private String roleId;

    @Column(nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserRole> userRoles = new HashSet<>();

}