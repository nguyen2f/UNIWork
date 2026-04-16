package com.uniwork.modules.auth.controller;

import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.auth.request.LoginRequest;
import com.uniwork.modules.auth.request.RegisterRequest;
import com.uniwork.modules.user.service.UserService;
import com.uniwork.modules.auth.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Api register commented out as accounts are now created by Admin
    /*
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getName());
        User user = userService.registerUser(registerRequest);
        return ResponseEntity.ok(user);
    }
    */

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Logining user: {}", loginRequest.getEmail());
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("role", user.getSystemRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity logoutUser() {
        log.info("User logged out");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
