package com.uniwork.controller;


import com.uniwork.dto.ProjectRequest;
import com.uniwork.model.Project;
import com.uniwork.service.ProjectService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService ProjectService;

    @PostMapping("/create")
    public ResponseEntity createProject(ProjectRequest projectRequest) {
        log.info("Creating project with request: {}", projectRequest);
        Project project = ProjectService.createProject(projectRequest);
        return ResponseEntity.ok(project);
    }
}
