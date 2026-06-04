package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleProjectReportDTO {
    private Long projectId;
    private String projectName;
    private String projectStatus;
    private String projectMethod;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Task breakdown
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Long reviewingTasks;
    private Long cancelledTasks;
    private Double taskCompletedPercent;

    // Issue breakdown
    private Long totalIssues;
    private Long openIssues;
    private Long inProgressIssues;
    private Long resolvedIssues;
    private Long closedIssues;

    // Members
    private Long totalMembers;

    // Stages
    private Long totalStages;
    private Long completedStages;
    private Long activeStages;
}
