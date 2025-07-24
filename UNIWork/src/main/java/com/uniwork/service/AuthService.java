package com.uniwork.service;

import com.uniwork.model.User;
import com.uniwork.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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
            throw new RuntimeException("Token không hợp lệ hoặc thiếu");
        }

        String token = authHeader.substring(7); // Bỏ "Bearer "
        Claims claims = jwtUtil.extractAllClaims(token);

        Object rawUserId = claims.get("id");
        if (rawUserId == null) {
            throw new RuntimeException("Không tìm thấy userId trong token");
        }

        return Long.valueOf(rawUserId.toString()); // an toàn nếu token encode là string/number
    }
}
