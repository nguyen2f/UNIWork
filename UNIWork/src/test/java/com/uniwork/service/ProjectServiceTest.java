package com.uniwork.service;

import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.enums.Role;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.modules.project.dto.ProjectDTO;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.project.repository.ProjectMemberRepository;
import com.uniwork.modules.project.repository.ProjectRepository;
import com.uniwork.modules.project.request.ProjectRequest;
import com.uniwork.modules.project.service.ProjectServiceImpl;
import com.uniwork.modules.stage.service.StageService;
import com.uniwork.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StageService stageService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setProjectId(100L);
        testProject.setName("Test Enterprise Project");
        testProject.setDescription("Project description");
        testProject.setOwnerId(1L);
        testProject.setStatus(ProjectStatus.PLANNING);
        testProject.setPriority(Priority.HIGH);
        testProject.setMethod(ProjectMethod.AGILE);
        testProject.setCreatedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("getAllProjectsByUserId should return projects with member count using optimized batch query")
    void testGetAllProjectsByUserId_Success() {
        Long userId = 1L;
        List<Project> mockProjects = List.of(testProject);
        when(projectRepository.findAllByUserIdAndFilter(eq(userId), any(), any()))
                .thenReturn(mockProjects);

        List<Object[]> memberCounts = new ArrayList<>();
        memberCounts.add(new Object[]{100L, 5L});
        when(projectMemberRepository.countMembersByProjectIds(List.of(100L)))
                .thenReturn(memberCounts);

        List<ProjectDTO> result = projectService.getAllProjectsByUserId(userId, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProjectId()).isEqualTo(100L);
        assertThat(result.get(0).getTotalMembers()).isEqualTo(5L);

        verify(projectRepository, times(1)).findAllByUserIdAndFilter(eq(userId), any(), any());
        verify(projectMemberRepository, times(1)).countMembersByProjectIds(List.of(100L));
    }

    @Test
    @DisplayName("createProject should save project, assign owner, and initialize stages")
    void testCreateProject_Success() {
        Long userId = 1L;
        ProjectRequest request = new ProjectRequest();
        request.setName("New Project");
        request.setDescription("New Desc");
        request.setMethod("AGILE");
        request.setPriority(2);

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setProjectId(101L);
            return p;
        });

        ProjectDTO created = projectService.createProject(request, userId);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("New Project");

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        verify(stageService, times(1)).initDefaultStages(eq(101L), eq(ProjectMethod.AGILE));
    }

    @Test
    @DisplayName("updateProject should throw FORBIDDEN when user is not owner")
    void testUpdateProject_WhenNotOwner_ThrowsForbidden() {
        Long projectId = 100L;
        Long userId = 2L;

        // Current member is not owner
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(member);

        ProjectRequest request = new ProjectRequest();
        request.setName("Hacked Name");

        CoreException ex = assertThrows(CoreException.class, () ->
                projectService.updateProject(projectId, request, userId)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteProject should throw FORBIDDEN when user is not owner")
    void testDeleteProject_WhenNotOwner_ThrowsForbidden() {
        Long projectId = 100L;
        Long userId = 99L;

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(null);

        CoreException ex = assertThrows(CoreException.class, () ->
                projectService.deleteProject(projectId, userId)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
    }
}
