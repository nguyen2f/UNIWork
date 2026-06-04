package com.uniwork.modules.report.projection;

import java.time.LocalDateTime;

public interface ReportOverdueIssueProjection {
    Long getIssueId();
    String getTitle();
    String getStatus();
    String getPriority();
    LocalDateTime getDueDate();
    Long getAssignedTo();
    String getAssigneeName();
    Long getProjectId();
    String getProjectName();
}
