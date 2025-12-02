package com.uniwork.model.projection;

public interface ReportTaskStatsProjection {
    Long getCompletedTasks();
    Long getNewTasksThisWeek();
    Long getPendingTasks();
    Long getPendingTasksLastWeek();

}
