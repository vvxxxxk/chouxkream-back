package com.kream.chouxkream.oauth2.model.dto;

import com.kream.chouxkream.user.model.entity.Role;
import com.kream.chouxkream.user.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class OAuth2UserImpl implements OAuth2User {

    User user;
    Role role;

    public OAuth2UserImpl(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // role 값 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role.getRoleName();
            }
        });

        return collection;
    }

    // email을 리턴하도록 수정
    @Override
    public String getName() {

        return user.getEmail();
    }

    public String getUsername() {

        return user.getUsername();
    }
}
