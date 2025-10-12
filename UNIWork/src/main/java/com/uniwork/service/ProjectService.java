package com.uniwork.service;

import com.uniwork.entity.dto.UserDTO;
import com.uniwork.entity.enumuration.Priority;
import com.uniwork.entity.enumuration.ProjectStatus;
import com.uniwork.entity.request.ProjectRequest;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.ProjectMember;
import com.uniwork.entity.model.User;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.UserRepository;
import com.uniwork.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Project getProjectDetail(Long projectId, Long userId) {
        checkProjectMember(projectId, userId);
        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }
        return project;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getAllProjectsByUserId(Long userId, Integer priority, Integer status) {
        List<Project> projects = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserId(userId);
        for (ProjectMember pm : projectMembers) {
            Long projectId = pm.getProjectId();
            Project project = projectRepository.findProjectByProjectIdAndFilter(projectId, Priority.fromCode(priority), ProjectStatus.fromCode(status));
            projects.add(project);
        }
        return projects;
    }

    public Project createProject(ProjectRequest projectRequest, Long userId) {
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());
        project.setOwnerId(userId);
        project.setCreatedDate(LocalDateTime.now());
        project.setStatus(ProjectStatus.PLANNING);
        project.setClient(projectRequest.getClient());
        project.setDepartment(projectRequest.getDepartment());
        project.setRiskLevel(projectRequest.getRiskLevel());
        project.setCategory(projectRequest.getCategory());
        project.setPriority(Priority.fromCode(projectRequest.getPriority()));
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(project.getProjectId());
        projectMember.setUserId(userId);
        projectMember.setRole("OWNER");
        projectMember.setStatus(true);
        projectMemberRepository.save(projectMember);
        return project;

    }

    public Project updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }
        BeanCopyUtils.copyNonNullProperties(projectRequest, project);
        return projectRepository.save(project);

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
