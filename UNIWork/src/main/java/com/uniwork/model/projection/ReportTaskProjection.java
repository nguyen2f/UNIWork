package com.uniwork.model.projection;

public interface ReportTaskProjection {
    Long getTotalTasks();
    Long getCompletedTasks();
    Long getPendingTasks();
    Long getReviewingTasks();
    Long getCancelledTasks();
    Long getDoingTasks();
}
