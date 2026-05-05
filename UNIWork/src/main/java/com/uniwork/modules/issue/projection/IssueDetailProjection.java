package com.uniwork.modules.issue.projection;

import java.time.LocalDateTime;

public interface IssueDetailProjection {
    Long getIssueId();
    Long getTaskId();
    Long getProjectId();
    Long getReportedBy();
    Long getAssignedTo();
    String getTitle();
    String getDescription();
    String getType();
    String getPriority();
    String getStatus();
    LocalDateTime getDueDate();
    LocalDateTime getCreatedDate();
    LocalDateTime getUpdatedDate();
    String getReporterName();
    String getAssigneeName();
    String getTaskTitle();
}
