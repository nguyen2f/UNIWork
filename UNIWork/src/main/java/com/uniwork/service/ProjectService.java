package com.uniwork.service;

import com.uniwork.dto.ProjectRequest;
import com.uniwork.model.Project;
import com.uniwork.model.ProjectMember;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public Project getProjectById(Long projectId) {
        return projectRepository.findProjectByProjectId(projectId);
    }

    public ResponseEntity getProjectDetail(Long projectId, Long userId) {
        checkProjectMember(projectId, userId);
        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        return ResponseEntity.ok(project);
    }

    public ResponseEntity getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    public ResponseEntity getAllProjectsByUserId(Long userId) {
        List<Project> projects = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserId(userId);
        for (ProjectMember pm : projectMembers) {
            Project project = projectRepository.findProjectByProjectId(pm.getProjectId());
            projects.add(project);
        }
        return ResponseEntity.ok(projects);
    }

    public ResponseEntity createProject(ProjectRequest projectRequest, Long userId) {
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());
        project.setOwnerId(userId);
        project.setCreatedDate(new Date());
        project.setStatus("PENDING");
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(project.getProjectId());
        projectMember.setUserId(userId);
        projectMember.setRole("OWNER");
        projectMember.setStatus(true);
        projectMemberRepository.save(projectMember);
        return ResponseEntity.ok(project);

    }

    public ResponseEntity updateProject(ProjectRequest projectRequest) {
        Project project = projectRepository.findProjectByProjectId(projectRequest.getId());
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());
        return ResponseEntity.ok(projectRepository.save(project));

    }

    private Boolean checkProjectMember(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null;
    }
}
