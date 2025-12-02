package com.uniwork.model.projection;

public interface ReportProjectProjection {
    Long getProjectId();
    String getName();
    Long getTotalMembers();
    Long getTotalTasks();
    Long getCompletedTasks();
    Long getPendingTasks();
    Long getReviewingTasks();
    Long getCancelledTasks();
    Long getDoingTasks();
}
