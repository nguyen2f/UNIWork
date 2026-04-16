package com.uniwork.modules.report.projection;

public interface ReportTaskPerformanceProjection {
    Long getTotalTasks();
    Long getCompletedBeforeDeadline();
}
