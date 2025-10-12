package com.uniwork.filter;

import com.uniwork.interceptors.Payload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PayloadFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-resources")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("authorization");
        String userId = request.getHeader("userId");
        if (token != null && userId != null) {
            Payload payload = new Payload();
            payload.setToken(token);
            payload.setUserId(Long.parseLong(userId));
            request.setAttribute("payload", payload);
        }

        filterChain.doFilter(request, response);
    }
}
