package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryDTO {
    // Overall counts
    private Long totalTasks;
    private Long totalIssues;
    private Long totalProjects;

    // Completion metrics
    private Long completedTasks;
    private Long completedIssues;
    private Double taskCompletionRate;
    private Double issueCompletionRate;

    // Overdue metrics
    private Long overdueTasks;
    private Long overdueIssues;
    private Double overdueRate;

    // Average completion time (in days)
    private Double avgTaskCompletionDays;
    private Double avgIssueResolutionDays;

    // Distributions
    private List<StatusDistributionDTO> taskStatusDistribution;
    private List<StatusDistributionDTO> issueStatusDistribution;
    private List<PriorityDistributionDTO> taskPriorityDistribution;
    private List<PriorityDistributionDTO> issuePriorityDistribution;
}
