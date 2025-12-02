package com.uniwork.controller;


import com.uniwork.model.dto.UserDTO;
import com.uniwork.model.entity.Project;
import com.uniwork.model.request.ProjectRequest;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/all")
    public ResponseEntity getAllProjects(@RequestAttribute(required = false) Payload payload,
                                         @RequestParam(required = false) Integer priority,
                                         @RequestParam(required = false) Integer status) {
        log.info("Getting all projects for userId: {}", payload.getUserId());
        List<Project> projects = projectService.getAllProjectsByUserId(payload.getUserId(), priority, status);
        return ResponseFactory.success(projects);
    }

    @GetMapping("/detail/{projectId}")
    public ResponseEntity getProjectDetail(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Getting project detail for projectId: {} and userId: {}", projectId, payload.getUserId());
        Project project = projectService.getProjectDetail(projectId, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @PostMapping("/create")
    public ResponseEntity createProject(@RequestBody ProjectRequest projectRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Creating project with request: {}", projectRequest);
        Project project = projectService.createProject(projectRequest, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @PostMapping("/{projectId}/update")
    public ResponseEntity updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Updating project with ID: {} and request: {}", projectId, projectRequest);
        Project project = projectService.updateProject(projectId, projectRequest, payload.getUserId());
        return ResponseFactory.success(project);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity getProjectMembers(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Getting members for projectId: {} and userId: {}", projectId, payload.getUserId());
        List<UserDTO> members = projectService.findAllMembersByProjectId(projectId);
        return ResponseFactory.success(members);
    }
}
