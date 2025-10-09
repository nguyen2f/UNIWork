package com.uniwork.controller;
import com.uniwork.entity.request.AssignMemberRequest;
import com.uniwork.entity.request.LoginRequest;
import com.uniwork.entity.request.RegisterRequest;
import com.uniwork.entity.request.UpdateProfileRequest;
import com.uniwork.interceptors.Payload;
import com.uniwork.entity.model.User;
import com.uniwork.service.UserService;
import com.uniwork.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity registerUser(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getName());
        User user = userService.registerUser(registerRequest);
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

    @PutMapping("/update-profile")
    public ResponseEntity updateProfile(@RequestAttribute Payload payload, @RequestBody UpdateProfileRequest updateProfileRequest) {
        return ResponseEntity.ok(userService.updateProfile(payload.getUserId(), updateProfileRequest));
    }

    @PostMapping("/assign-member")
    public ResponseEntity assignMemberToProject(@RequestAttribute Payload payload, @RequestBody AssignMemberRequest assignMemberRequest) {
        return ResponseEntity.ok(userService.assignMemberToProject(assignMemberRequest));

    }
}
