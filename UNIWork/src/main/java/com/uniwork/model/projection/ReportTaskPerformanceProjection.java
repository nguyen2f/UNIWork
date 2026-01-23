package com.uniwork.model.projection;

public interface ReportTaskPerformanceProjection {
    Long getTotalTasks();
    Long getCompletedBeforeDeadline();
}
