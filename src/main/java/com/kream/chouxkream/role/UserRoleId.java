package com.kream.chouxkream.role;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserRoleId implements Serializable {

    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "role_id")
    private String roleId;
}