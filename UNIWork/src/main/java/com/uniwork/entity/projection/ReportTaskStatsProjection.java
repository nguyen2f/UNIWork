package com.uniwork.entity.projection;

public interface ReportTaskStatsProjection {
    Long getCompletedTasks();
    Long getNewTasksThisWeek();
    Long getPendingTasks();
    Long getPendingTasksLastWeek();

}
