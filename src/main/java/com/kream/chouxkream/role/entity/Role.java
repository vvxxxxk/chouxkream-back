package com.kream.chouxkream.role.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Role {
    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name="role_name")
    private String name;
}
