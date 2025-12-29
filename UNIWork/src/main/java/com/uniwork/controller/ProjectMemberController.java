package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.model.entity.ProjectMember;
import com.uniwork.model.request.AssignMemberRequest;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/project-member")
@RestController
@Slf4j
public class ProjectMemberController {

    @Autowired
    private UserService userService;

    @PostMapping("/assign")
    public ResponseEntity assignMemberToProject(@RequestAttribute(required = false) Payload payload, @RequestBody AssignMemberRequest assignMemberRequest) {
        ProjectMember projectMember = userService.assignMemberToProject(assignMemberRequest);
        return ResponseFactory.success(projectMember);
    }
}
