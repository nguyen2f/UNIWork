package com.uniwork.controller;


import com.uniwork.dto.ProjectRequest;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/get-all")
    public ResponseEntity getAllProjects(@RequestAttribute Payload payload) {
        log.info("Getting all projects for userId: {}", payload.getUserId());
        return projectService.getAllProjectsByUserId(payload.getUserId());
    }

    @GetMapping("/get-detail/{projectId}")
    public ResponseEntity getProjectDetail(@PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Getting project detail for projectId: {} and userId: {}", projectId, payload.getUserId());
        return projectService.getProjectDetail(projectId, payload.getUserId());
    }

    @PostMapping("/create")
    public ResponseEntity createProject(@RequestBody ProjectRequest projectRequest, @RequestAttribute Payload payload) {
        log.info("Creating project with request: {}", projectRequest);
        return projectService.createProject(projectRequest, payload.getUserId());
    }


}
