package com.uniwork.modules.auth.service;

import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.auth.service.AuthService;
import com.uniwork.modules.user.service.UserService;
import com.uniwork.modules.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public User validateToken(String token, String email) {
        boolean valid = jwtUtil.validateToken(token, email);
        if (!valid) {
            return null;
        }
        return userService.findByEmail(email);
    }

    public Long getUserIdFromToken() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractAllClaims(token);

        Object rawUserId = claims.get("id");
        if (rawUserId == null) {
            throw new RuntimeException("Can not find User");
        }

        return Long.valueOf(rawUserId.toString());
    }
}
