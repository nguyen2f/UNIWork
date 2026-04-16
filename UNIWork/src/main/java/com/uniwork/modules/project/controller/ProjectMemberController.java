package com.uniwork.modules.project.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/project-member")
@RestController
@Slf4j
@PreAuthorize("hasAuthority('PERM_MANAGE_PROJECT_MEMBERS')")
public class ProjectMemberController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('PERM_ASSIGN_MEMBERS')")
    @PostMapping("/assign")
    public ResponseEntity assignMemberToProject(@RequestAttribute(required = false) Payload payload, @RequestBody AssignMemberRequest assignMemberRequest) {
        ProjectMember projectMember = userService.assignMemberToProject(assignMemberRequest);
        return ResponseFactory.success(projectMember);
    }
}
