package com.uniwork.modules.project.service;

import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.project.request.ProjectRequest;

import java.util.List;

public interface ProjectService {

    Project getProjectById(Long projectId);

    Project getProjectDetail(Long projectId, Long userId);

    List<Project> getAllProjectsByUserId(Long userId, Integer priority, Integer status);

    Project createProject(ProjectRequest projectRequest, Long userId);

    Project updateProject(Long projectId, ProjectRequest projectRequest, Long userId);

    List<UserDTO> findAllMembersByProjectId(Long projectId);

    Boolean checkProjectMember(Long projectId, Long userId);
}
