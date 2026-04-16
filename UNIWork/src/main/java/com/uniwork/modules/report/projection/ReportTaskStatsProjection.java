package com.uniwork.modules.report.projection;

public interface ReportTaskStatsProjection {
    Long getCompletedTasks();
    Long getNewTasksThisWeek();
    Long getPendingTasks();
    Long getPendingTasksLastWeek();

}
