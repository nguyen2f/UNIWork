package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.model.dto.ProfileDTO;
import com.uniwork.model.dto.UserDTO;
import com.uniwork.model.entity.ProjectMember;
import com.uniwork.model.entity.User;
import com.uniwork.model.request.*;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.service.UserService;
import com.uniwork.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getName());
        User user = userService.registerUser(registerRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Logining user: {}", loginRequest.getEmail());
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("role", user.getSystemRole());
        response.put("user", user);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity updateProfile(@RequestAttribute(required = false) Payload payload, @RequestBody UpdateProfileRequest updateProfileRequest) {
        User user = userService.updateProfile(payload.getUserId(), updateProfileRequest);
        return ResponseFactory.success(user);
    }

    @PostMapping("/member/assign")
    public ResponseEntity assignMemberToProject(@RequestAttribute(required = false) Payload payload, @RequestBody AssignMemberRequest assignMemberRequest) {
        ProjectMember projectMember = userService.assignMemberToProject(assignMemberRequest);
        return ResponseFactory.success(projectMember);
    }

    @PostMapping("/member/remove")
    public ResponseEntity removeMemberFromProject(@RequestAttribute(required = false) Payload payload, @RequestBody RemoveMemberRequest removeMemberRequest) {
        ProjectMember projectMember = userService.removeMemberFromProject(removeMemberRequest);
        return ResponseFactory.success(projectMember);
    }

    @GetMapping("/all")
    public ResponseEntity getAllMemberActive(@RequestAttribute(required = false) Payload payload) {
        List<UserDTO> users = userService.getAllMembersActive();
        return ResponseFactory.success(users);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity getUserById(@RequestAttribute(required = false) Payload payload, @PathVariable Long userId) {
        ProfileDTO user = userService.getUserById(userId);
        return ResponseFactory.success(user);
    }
}
