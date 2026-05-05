package com.uniwork.modules.project.service;

import com.uniwork.modules.project.dto.ProjectDTO;
import com.uniwork.modules.project.dto.ProjectDetailDTO;
import com.uniwork.modules.project.dto.ProjectMemberDTO;
import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.enums.Role;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.modules.project.request.ProjectRequest;
import com.uniwork.modules.project.request.UpdateProjectStatusRequest;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.stage.dto.StageDetailDTO;
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
import org.springframework.context.annotation.Lazy;
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
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private StageService stageService;

    public Project getProjectById(Long projectId) {
        return projectRepository.findProjectByProjectId(projectId);
    }

    public ProjectDetailDTO getProjectDetail(Long projectId, Long userId) {
        checkProjectMember(projectId, userId);
        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find project");
        }

        ProjectDTO projectDTO = buildProjectDTO(project);

        // Get members
        List<ProjectMemberDTO> members = getProjectMembers(projectId);

        // Get stages with detail
        List<StageDetailDTO> stages = stageService.getStagesByProject(projectId)
                .stream()
                .map(stage -> stageService.getStageDetail(stage.getStageId()))
                .collect(Collectors.toList());

        return ProjectDetailDTO.builder()
                .project(projectDTO)
                .members(members.stream()
                        .map(m -> new UserDTO(m.getUserId(), m.getUserName(), m.getEmail(), null, null))
                        .collect(Collectors.toList()))
                .stages(stages)
                .build();
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<ProjectDTO> getAllProjectsByUserId(Long userId, Integer priority, Integer status) {
        List<ProjectDTO> projects = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserId(userId);
        for (ProjectMember pm : projectMembers) {
            Long projectId = pm.getProjectId();
            Project project = projectRepository.findProjectByProjectIdAndFilter(projectId, Priority.fromCode(priority), ProjectStatus.fromCode(status));
            if (project != null) {
                projects.add(buildProjectDTO(project));
            }
        }
        return projects;
    }

    public ProjectDTO createProject(ProjectRequest projectRequest, Long userId) {
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());
        project.setOwnerId(userId);
        project.setCreatedDate(LocalDateTime.now());
        project.setStatus(ProjectStatus.PLANNING);
        project.setClient(projectRequest.getClient());
        project.setDepartmentId(projectRequest.getDepartmentId());
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

        return buildProjectDTO(project);
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

    public ProjectDTO updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "This member is not in this project");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }
        BeanCopyUtils.copyNonNullProperties(projectRequest, project);
        Project savedProject = projectRepository.save(project);
        return buildProjectDTO(savedProject);
    }

    public ProjectDTO updateProjectStatus(Long projectId, UpdateProjectStatusRequest request, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Only project owner can change status");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }

        ProjectStatus newStatus = ProjectStatus.fromCode(request.getStatus());
        if (newStatus == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Invalid project status");
        }

        project.setStatus(newStatus);
        project.setUpdatedDate(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        return buildProjectDTO(savedProject);
    }

    public void deleteProject(Long projectId, Long userId) {
        if (!checkProjectOwner(projectId, userId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Only project owner can delete project");
        }

        Project project = projectRepository.findProjectByProjectId(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }

        project.setIsDeleted(true);
        project.setUpdatedDate(LocalDateTime.now());
        projectRepository.save(project);
    }

    public Boolean checkProjectMember(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null;
    }

    public Boolean checkProjectOwner(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return projectMember != null && projectMember.getRole() == Role.OWNER;
    }

    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectId);
        List<Long> userIds = projectMembers.stream()
                .map(ProjectMember::getUserId)
                .collect(Collectors.toList());

        List<User> users = userRepository.findAllByUserIdIn(userIds);

        return projectMembers.stream().map(pm -> {
            User user = users.stream()
                    .filter(u -> u.getUserId().equals(pm.getUserId()))
                    .findFirst()
                    .orElse(null);

            return ProjectMemberDTO.builder()
                    .pmId(pm.getPmId())
                    .userId(pm.getUserId())
                    .userName(user != null ? user.getName() : null)
                    .email(user != null ? user.getEmail() : null)
                    .role(pm.getRole())
                    .build();
        }).collect(Collectors.toList());
    }

    public ProjectMemberDTO assignMember(Long projectId, AssignMemberRequest request) {
        // Check if already a member
        ProjectMember existing = projectMemberRepository.findByProjectIdAndUserId(projectId, request.getUserId());
        if (existing != null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "User is already a member of this project");
        }

        // Verify user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CoreException(ErrorCode.INTERNAL_ERROR, "User not found"));

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            role = Role.MEMBER;
        }

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        projectMember.setUserId(request.getUserId());
        projectMember.setRole(role);
        projectMember.setStatus(true);
        projectMemberRepository.save(projectMember);

        return ProjectMemberDTO.builder()
                .pmId(projectMember.getPmId())
                .userId(user.getUserId())
                .userName(user.getName())
                .email(user.getEmail())
                .role(role)
                .build();
    }

    public void removeMember(Long projectId, Long userId, Long currentUserId) {
        // Cannot remove yourself if you are the owner
        ProjectMember currentMember = projectMemberRepository.findByProjectIdAndUserId(projectId, currentUserId);
        ProjectMember targetMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);

        if (targetMember == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "User is not a member of this project");
        }

        if (targetMember.getRole() == Role.OWNER) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Cannot remove the project owner");
        }

        targetMember.setIsDeleted(true);
        projectMemberRepository.save(targetMember);
    }

    // =====================================================
    // PRIVATE HELPERS
    // =====================================================

    private ProjectDTO buildProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO(project);

        // Add summary stats
        Long totalMembers = projectMemberRepository.countUserIdByProjectId(project.getProjectId());
        dto.setTotalMembers(totalMembers);

        return dto;
    }
}
