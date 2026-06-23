package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAnalyticsDTO {
    private Long projectId;
    private String projectName;

    // Task metrics
    private Long totalTasks;
    private Long completedTasks;
    private Double taskCompletionRate;

    // Issue metrics
    private Long totalIssues;
    private Long resolvedIssues;
    private Double issueResolutionRate;

    // Velocity - items completed in recent period
    private Long tasksCompletedThisWeek;
    private Long tasksCompletedThisMonth;
    private Long issuesResolvedThisWeek;
    private Long issuesResolvedThisMonth;

    // Time-based
    private LocalDateTime projectStartDate;
    private LocalDateTime projectEndDate;
    private Long daysRemaining;
    private Double dailyTaskVelocity; // avg tasks completed per day

    // Status distributions
    private List<StatusDistributionDTO> taskStatusDistribution;
    private List<StatusDistributionDTO> issueStatusDistribution;
}
