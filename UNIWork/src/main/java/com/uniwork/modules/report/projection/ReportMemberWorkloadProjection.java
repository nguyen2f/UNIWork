package com.uniwork.modules.report.projection;

public interface ReportMemberWorkloadProjection {
    Long getUserId();
    String getUserName();
    Long getTotalTasks();
    Long getCompletedTasks();
    Long getPendingTasks();
    Long getDoingTasks();
}
