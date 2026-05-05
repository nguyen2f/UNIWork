package com.uniwork.modules.project.controller;


import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.project.dto.ProjectDTO;
import com.uniwork.modules.project.dto.ProjectDetailDTO;
import com.uniwork.modules.project.dto.ProjectMemberDTO;
import com.uniwork.modules.project.request.ProjectRequest;
import com.uniwork.modules.project.request.UpdateProjectStatusRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects")
@PreAuthorize("hasAuthority('PERM_MANAGE_PROJECTS')")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity getAllProjects(@RequestAttribute(required = false) Payload payload,
                                         @RequestParam(required = false) Integer priority,
                                         @RequestParam(required = false) Integer status) {
        log.info("Getting all projects for userId: {}", payload.getUserId());
        List<ProjectDTO> projects = projectService.getAllProjectsByUserId(payload.getUserId(), priority, status);
        return ResponseFactory.success(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity getProjectDetail(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Getting project detail for projectId: {} and userId: {}", projectId, payload.getUserId());
        ProjectDetailDTO projectDetail = projectService.getProjectDetail(projectId, payload.getUserId());
        return ResponseFactory.success(projectDetail);
    }

    @PreAuthorize("hasAuthority('PERM_CREATE_PROJECT')")
    @PostMapping
    public ResponseEntity createProject(@RequestBody ProjectRequest projectRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Creating project with request: {}", projectRequest);
        ProjectDTO project = projectService.createProject(projectRequest, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @PreAuthorize("hasAuthority('PERM_UPDATE_PROJECT')")
    @PutMapping("/{projectId}")
    public ResponseEntity updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Updating project with ID: {} and request: {}", projectId, projectRequest);
        ProjectDTO project = projectService.updateProject(projectId, projectRequest, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @PreAuthorize("hasAuthority('PERM_UPDATE_PROJECT')")
    @PutMapping("/{projectId}/status")
    public ResponseEntity updateProjectStatus(@PathVariable Long projectId,
                                               @RequestBody UpdateProjectStatusRequest request,
                                               @RequestAttribute(required = false) Payload payload) {
        log.info("Updating project status for projectId: {}", projectId);
        ProjectDTO project = projectService.updateProjectStatus(projectId, request, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @PreAuthorize("hasAuthority('PERM_UPDATE_PROJECT')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting project with ID: {}", projectId);
        projectService.deleteProject(projectId, payload.getUserId());
        return ResponseFactory.success("Project deleted successfully");
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity getProjectMembers(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Getting members for projectId: {} and userId: {}", projectId, payload.getUserId());
        List<ProjectMemberDTO> members = projectService.getProjectMembers(projectId);
        return ResponseFactory.success(members);
    }
}
