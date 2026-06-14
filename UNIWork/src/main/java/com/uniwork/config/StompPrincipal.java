package com.uniwork.config;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

// StompPrincipal.java
@RequiredArgsConstructor
public class StompPrincipal implements Principal {
    private final String name;

    @Override
    public String getName() {
        return name;
    }
}
