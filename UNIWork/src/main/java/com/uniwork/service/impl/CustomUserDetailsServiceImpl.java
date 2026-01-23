package com.uniwork.service.impl;

import com.uniwork.config.AuthorizationMatrix;
import com.uniwork.model.entity.User;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthorizationMatrix authorizationMatrix;

    public CustomUserDetailsServiceImpl(
            UserRepository userRepository,
            AuthorizationMatrix authorizationMatrix) {
        this.userRepository = userRepository;
        this.authorizationMatrix = authorizationMatrix;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByName(username);
        return new CustomUserDetails(user, authorizationMatrix);
    }
}
