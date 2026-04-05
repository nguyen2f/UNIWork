package com.uniwork.model.dto;

import com.uniwork.model.projection.TaskDetailProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long taskId;
    private Long projectId;
    private Long assignedTo;
    private Long createdBy;

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

    private Long taskParentId;

    // Stage info
    private Long stageId;
    private String stageName;

    public TaskDTO(TaskDetailProjection p) {
        this.taskId = p.getTaskId();
        this.projectId = p.getProjectId();
        this.assignedTo = p.getAssignedTo();
        this.createdBy = p.getCreatedBy();
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
        this.taskParentId = p.getTaskParentId();
        this.stageId = p.getStageId();
        this.stageName = p.getStageName();
    }

}
