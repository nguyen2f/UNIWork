package com.uniwork.modules.report.projection;

import java.time.LocalDateTime;

public interface ReportOverdueTaskProjection {
    Long getTaskId();
    String getTitle();
    String getStatus();
    String getPriority();
    LocalDateTime getDueDate();
    Long getAssignedTo();
    String getAssigneeName();
    Long getProjectId();
    String getProjectName();
}
