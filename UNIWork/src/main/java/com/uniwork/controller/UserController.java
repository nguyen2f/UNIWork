package com.uniwork.controller;
import com.uniwork.dto.LoginRequest;
import com.uniwork.dto.UserDTO;
import com.uniwork.model.User;
import com.uniwork.service.UserService;
import com.uniwork.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; // Giả sử bạn có một JwtUtil để tạo token

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody  UserDTO userDTO) {
        log.info("Registering user: {}", userDTO.getName());
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(
            @RequestBody LoginRequest loginRequest) {
        log.info("Logining user: {}", loginRequest.getEmail());
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        return ResponseEntity.ok(response);

    }
}
