package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserRoleKey implements Serializable {

    private Long userNo;
    private String roleId;
}
