package com.uniwork.service;

import com.uniwork.dto.ProjectRequest;
import com.uniwork.model.Project;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AuthService authService;

    public Project createProject(ProjectRequest projectRequest) {
        Long userId = authService.getUserIdFromToken(); // 👈 Gọi service
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());
        project.setOwnerId(userId);
        project.setCreatedDate(new Date());
        return projectRepository.save(project);
    }
}
