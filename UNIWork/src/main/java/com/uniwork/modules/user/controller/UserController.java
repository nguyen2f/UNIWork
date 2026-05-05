package com.uniwork.modules.user.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.user.dto.ProfileDTO;
import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.task.request.*;
import com.uniwork.modules.project.request.*;
import com.uniwork.modules.user.request.*;
import com.uniwork.modules.auth.request.*;
import com.uniwork.modules.stage.request.*;
import com.uniwork.modules.comment.request.*;
import com.uniwork.modules.event.request.*;
import com.uniwork.modules.file.request.*;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.user.service.UserService;
import com.uniwork.modules.auth.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Login and Register endpoints have been moved to AuthController

    @PutMapping("/profile/{userId}")
    public ResponseEntity updateProfile(@RequestAttribute(required = false) Payload payload, @RequestBody UpdateProfileRequest updateProfileRequest) {
        User user = userService.updateProfile(payload.getUserId(), updateProfileRequest);
        return ResponseFactory.success(user);
    }

    @PreAuthorize("hasAuthority('PERM_ASSIGN_MEMBERS')")
    @PostMapping("/member/assign")
    public ResponseEntity assignMemberToProject(@RequestAttribute(required = false) Payload payload, @RequestBody AssignMemberRequest assignMemberRequest) {
        ProjectMember projectMember = userService.assignMemberToProject(assignMemberRequest);
        return ResponseFactory.success(projectMember);
    }

    @PreAuthorize("hasAuthority('PERM_REMOVE_MEMBERS')")
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

    @PostMapping("/profile/avatar")
    public ResponseEntity updateAvatar(@RequestAttribute(required = false) Payload payload, @RequestParam("file") MultipartFile file) {
        ProfileDTO user = userService.updateAvatar(payload.getUserId(), file);
        return ResponseFactory.success(user);
    }
}
