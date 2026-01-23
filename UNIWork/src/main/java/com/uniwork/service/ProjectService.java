package com.uniwork.service;

import com.uniwork.model.dto.UserDTO;
import com.uniwork.model.entity.Project;
import com.uniwork.model.request.ProjectRequest;

import java.util.List;

public interface ProjectService {

    Project getProjectById(Long projectId);

    Project getProjectDetail(Long projectId, Long userId);

    List<Project> getAllProjectsByUserId(Long userId, Integer priority, Integer status);

    Project createProject(ProjectRequest projectRequest, Long userId);

    Project updateProject(Long projectId, ProjectRequest projectRequest, Long userId);

    List<UserDTO> findAllMembersByProjectId(Long projectId);
}
