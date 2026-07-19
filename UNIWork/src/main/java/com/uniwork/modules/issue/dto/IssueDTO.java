package com.uniwork.modules.issue.dto;

import com.uniwork.modules.issue.projection.IssueDetailProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {

    private Long issueId;
    private Long taskId;
    private Long projectId;
    private Long reportedBy;
    private Long assignedTo;

    private String title;
    private String description;
    private String type;
    private String priority;
    private String status;

    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String reporterName;
    private String assigneeName;
    private String taskTitle;
    private String projectName;

    public IssueDTO(IssueDetailProjection p) {
        this.issueId = p.getIssueId();
        this.taskId = p.getTaskId();
        this.projectId = p.getProjectId();
        this.reportedBy = p.getReportedBy();
        this.assignedTo = p.getAssignedTo();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.type = p.getType();
        this.priority = p.getPriority();
        this.status = p.getStatus();
        this.dueDate = p.getDueDate();
        this.createdDate = p.getCreatedDate();
        this.updatedDate = p.getUpdatedDate();
        this.reporterName = p.getReporterName();
        this.assigneeName = p.getAssigneeName();
        this.taskTitle = p.getTaskTitle();
        this.projectName = p.getProjectName();
    }
}
