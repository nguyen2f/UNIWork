package com.uniwork.modules.project.dto;

import com.uniwork.enums.Priority;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.ProjectStatus;
import com.uniwork.modules.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {

    private Long projectId;
    private String name;
    private String description;
    private ProjectMethod method;
    private Priority priority;
    private ProjectStatus status;
    private String category;
    private String client;
    private String riskLevel;
    private Long ownerId;
    private Long departmentId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdDate;

    // Summary stats
    private Long totalMembers;
    private Long totalStages;
    private Long totalTasks;

    public ProjectDTO(Project project) {
        this.projectId = project.getProjectId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.method = project.getMethod();
        this.priority = project.getPriority();
        this.status = project.getStatus();
        this.category = project.getCategory();
        this.client = project.getClient();
        this.riskLevel = project.getRiskLevel();
        this.ownerId = project.getOwnerId();
        this.departmentId = project.getDepartmentId();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.createdDate = project.getCreatedDate();
    }
}
