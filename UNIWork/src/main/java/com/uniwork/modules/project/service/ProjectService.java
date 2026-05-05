package com.uniwork.modules.project.service;

import com.uniwork.modules.project.dto.ProjectDTO;
import com.uniwork.modules.project.dto.ProjectDetailDTO;
import com.uniwork.modules.project.dto.ProjectMemberDTO;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.modules.project.request.ProjectRequest;
import com.uniwork.modules.project.request.UpdateProjectStatusRequest;

import java.util.List;

public interface ProjectService {

    Project getProjectById(Long projectId);

    ProjectDetailDTO getProjectDetail(Long projectId, Long userId);

    List<ProjectDTO> getAllProjectsByUserId(Long userId, Integer priority, Integer status);

    ProjectDTO createProject(ProjectRequest projectRequest, Long userId);

    ProjectDTO updateProject(Long projectId, ProjectRequest projectRequest, Long userId);

    ProjectDTO updateProjectStatus(Long projectId, UpdateProjectStatusRequest request, Long userId);

    void deleteProject(Long projectId, Long userId);

    List<ProjectMemberDTO> getProjectMembers(Long projectId);

    ProjectMemberDTO assignMember(Long projectId, AssignMemberRequest request);

    void removeMember(Long projectId, Long userId, Long currentUserId);

    Boolean checkProjectMember(Long projectId, Long userId);
}
