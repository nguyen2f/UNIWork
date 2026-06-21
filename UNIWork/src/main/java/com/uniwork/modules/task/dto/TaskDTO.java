package com.uniwork.modules.task.dto;

import com.uniwork.modules.task.projection.TaskDetailProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long taskId;
    private Long projectId;
    private Long assignedTo;
    private Long createdBy;
    private Long managedBy;

    private String title;
    private String description;
    private String priority;
    private String status;

    private Boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String tags;
    private String assigneeName;
    private String createdByName;
    private String managedByName;

//    private Long taskParentId;

    // Stage info
    private Long stageId;
    private String stageName;

    // Project info
    private String projectName;

    // Issue count
    private Long issueCount;

    public TaskDTO(TaskDetailProjection p) {
        this.taskId = p.getTaskId();
        this.projectId = p.getProjectId();
        this.assignedTo = p.getAssignedTo();
        this.createdBy = p.getCreatedBy();
        this.managedBy = p.getManagedBy();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.priority = p.getPriority();
        this.status = p.getStatus();
        this.completed = p.getCompleted();
        this.dueDate = p.getDueDate();
        this.createdDate = p.getCreatedDate();
        this.updatedDate = p.getUpdatedDate();
        this.tags = p.getTags();
        this.assigneeName = p.getAssigneeName();
        this.createdByName = p.getCreatedByName();
        this.managedByName = p.getManagedByName();
//        this.taskParentId = p.getTaskParentId();
        this.stageId = p.getStageId();
        this.stageName = p.getStageName();
        this.projectName = p.getProjectName();
        this.issueCount = p.getIssueCount();
    }

}
