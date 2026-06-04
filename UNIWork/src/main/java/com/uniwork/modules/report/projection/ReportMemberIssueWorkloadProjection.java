package com.uniwork.modules.report.projection;

public interface ReportMemberIssueWorkloadProjection {
    Long getUserId();
    String getUserName();
    Long getTotalIssues();
    Long getCompletedIssues();
    Long getPendingIssues();
}
