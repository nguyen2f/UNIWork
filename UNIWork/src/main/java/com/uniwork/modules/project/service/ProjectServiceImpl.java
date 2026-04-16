package com.uniwork.modules.project.service;

import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.enums.Role;
import com.uniwork.modules.project.request.ProjectRequest;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.user.entity.User;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.modules.project.repository.ProjectMemberRepository;
import com.uniwork.modules.project.repository.ProjectRepository;
import com.uniwork.modules.user.repository.UserRepository;
import com.uniwork.modules.auth.service.AuthService;
import com.uniwork.modules.project.service.ProjectService;
import com.uniwork.modules.stage.service.StageService;
import com.uniwork.modules.user.service.UserService;
import com.uniwork.common.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

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
    @Autowired
    private StageService stageService;

    public Project getProjectById(Long projectId) {
        return projectRepository.findProjectByProjectId(projectId);
    }

    public Project getProjectDetail(Long projectId, Long userId) {
        checkProjectMember(projectId, userId);
        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find project");
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
        project.setPriority(Priority.fromCode(projectRequest.getPriority()) != null ? Priority.fromCode(projectRequest.getPriority()) : Priority.HIGH);

        // Set project methodology (default to STANDARD if not specified)
        ProjectMethod method = resolveProjectMethod(projectRequest.getMethod());
        project.setMethod(method);

        projectRepository.save(project);

        // Create owner as project member
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(project.getProjectId());
        projectMember.setUserId(userId);
        projectMember.setRole(Role.OWNER);
        projectMember.setStatus(true);
        projectMemberRepository.save(projectMember);

        // Initialize default stages based on methodology
        stageService.initDefaultStages(project.getProjectId(), method);

        return project;
    }

    private ProjectMethod resolveProjectMethod(String methodStr) {
        if (methodStr == null || methodStr.isBlank()) {
            return ProjectMethod.STANDARD;
        }
        try {
            return ProjectMethod.valueOf(methodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProjectMethod.STANDARD;
        }
    }

    public Project updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "This member is not in this project");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }
        BeanCopyUtils.copyNonNullProperties(projectRequest, project);
        return projectRepository.save(project);

    }

    public Boolean checkProjectMember(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null;
    }

    public Boolean checkProjectOwner(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null && projectMember.getRole() == Role.OWNER;
    }

    public List<UserDTO> findAllMembersByProjectId(Long projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectId);
        List<Long> userIds = projectMembers.stream()
                .map(ProjectMember::getUserId)
                .collect(Collectors.toList());

        List<User> users = userRepository.findAllByUserIdIn(userIds);

        return users.stream()
                .map(u -> new UserDTO(u.getUserId(), u.getName(), u.getEmail(), u.getPhone(),u.getDepartment()))
                .collect(Collectors.toList());
    }
}
