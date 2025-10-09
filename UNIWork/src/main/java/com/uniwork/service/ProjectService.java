package com.uniwork.service;

import com.uniwork.entity.dto.UserDTO;
import com.uniwork.entity.request.ProjectRequest;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.ProjectMember;
import com.uniwork.entity.model.User;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.UserRepository;
import com.uniwork.util.BeanCopyUtils;
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
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

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
            Long projectId = pm.getProjectId();
            Project project = projectRepository.findProjectByProjectId(projectId);
//            List<UserDTO> members = findAllMembersByProjectId(projectId);
//            projects.add(members);
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
        project.setClient(projectRequest.getClient());
        project.setDepartment(projectRequest.getDepartment());
        project.setRiskLevel(projectRequest.getRiskLevel());
        project.setCategory(projectRequest.getCategory());
        project.setPriority(projectRequest.getPriority());
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(project.getProjectId());
        projectMember.setUserId(userId);
        projectMember.setRole("OWNER");
        projectMember.setStatus(true);
        projectMemberRepository.save(projectMember);
        return ResponseEntity.ok(project);

    }

    public ResponseEntity updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            return ResponseEntity.status(403).body("You are not the owner of this project");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }

        BeanCopyUtils.copyNonNullProperties(projectRequest, project);
        return ResponseEntity.ok(projectRepository.save(project));

    }

    private Boolean checkProjectMember(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null;
    }

    private Boolean checkProjectOwner(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null && projectMember.getRole().equals("OWNER");
    }

    public List<UserDTO> findAllMembersByProjectId(Long projectId) {
        List<UserDTO> users = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectId);
        for (ProjectMember pm : projectMembers) {
            User user = userRepository.findByUserId(pm.getUserId());
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
        }
        return users;
    }
}
