package com.uniwork.modules.issue.projection;

public interface ReportIssueProjection {
    Long getTotalIssues();
    Long getCompletedIssues();
    Long getPendingIssues();
    Long getDoingIssues();
}
