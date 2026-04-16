package com.uniwork.modules.auth.filter;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.enums.SystemRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PayloadFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken
                && auth.getPrincipal() instanceof Long
                && auth.getDetails() instanceof SystemRole) {

            Payload payload = new Payload();
            payload.setUserId((Long) auth.getPrincipal());
            payload.setRole((SystemRole) auth.getDetails());

            request.setAttribute("payload", payload);
        }

        filterChain.doFilter(request, response);
    }
}

