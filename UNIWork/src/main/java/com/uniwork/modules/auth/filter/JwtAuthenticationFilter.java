package com.uniwork.modules.auth.filter;

import com.uniwork.config.AuthorizationMatrix;
import com.uniwork.enums.SystemRole;
import com.uniwork.modules.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthorizationMatrix authorizationMatrix;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   AuthorizationMatrix authorizationMatrix) {
        this.jwtUtil = jwtUtil;
        this.authorizationMatrix = authorizationMatrix;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtil.extractUserId(token);
        SystemRole role = jwtUtil.extractRole(token);

        var authorities = authorizationMatrix
                .getPermission(role)
                .stream()
                .map(p -> new SimpleGrantedAuthority("PERM_" + p.name()))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );

        authentication.setDetails(role);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);


        filterChain.doFilter(request, response);
    }
}

