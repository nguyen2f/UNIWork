package com.uniwork.service;

import com.uniwork.config.AuthorizationMatrix;
import com.uniwork.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final AuthorizationMatrix authorizationMatrix;

    public CustomUserDetails(User user,
                             AuthorizationMatrix authorizationMatrix) {
        this.user = user;
        this.authorizationMatrix = authorizationMatrix;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorizationMatrix
                .getPermission(user.getSystemRole())
                .stream()
                .map(permission ->  new SimpleGrantedAuthority("PERM_" + permission.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
