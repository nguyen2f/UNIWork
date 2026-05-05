package com.uniwork.modules.project.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.project.dto.ProjectMemberDTO;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/projects/{projectId}/members")
@RestController
@Slf4j
@PreAuthorize("hasAuthority('PERM_MANAGE_PROJECT_MEMBERS')")
public class ProjectMemberController {

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasAuthority('PERM_ASSIGN_MEMBERS')")
    @PostMapping
    public ResponseEntity assignMemberToProject(@PathVariable Long projectId,
                                                 @RequestAttribute(required = false) Payload payload,
                                                 @RequestBody AssignMemberRequest assignMemberRequest) {
        log.info("Assigning member to project: {}", projectId);
        ProjectMemberDTO member = projectService.assignMember(projectId, assignMemberRequest);
        return ResponseFactory.success(member);
    }

    @PreAuthorize("hasAuthority('PERM_ASSIGN_MEMBERS')")
    @DeleteMapping("/{userId}")
    public ResponseEntity removeMemberFromProject(@PathVariable Long projectId,
                                                    @PathVariable Long userId,
                                                    @RequestAttribute(required = false) Payload payload) {
        log.info("Removing member {} from project: {}", userId, projectId);
        projectService.removeMember(projectId, userId, payload.getUserId());
        return ResponseFactory.success("Member removed successfully");
    }
}
