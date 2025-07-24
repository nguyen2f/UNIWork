package com.uniwork.controller;
import com.uniwork.dto.UserDTO;
import com.uniwork.model.User;
import com.uniwork.service.UserService;
import com.uniwork.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; // Giả sử bạn có một JwtUtil để tạo token

    @PostMapping("/register")
    public ResponseEntity registerUser(UserDTO userDTO) {
        log.info("Registering user: {}", userDTO.getName());
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(
            @RequestParam String email,
            @RequestParam String password) {
        log.info("Logining user: {}", email);
        User user = userService.login(email, password);
        String token = jwtUtil.generateToken(user); // Tạo token cho người dùng
        return ResponseEntity.ok(Collections.singletonMap("token", token)); // Trả về token cho client
    }
}
