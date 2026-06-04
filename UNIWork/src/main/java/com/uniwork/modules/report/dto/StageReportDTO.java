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
public class StageReportDTO {
    private Long stageId;
    private String stageName;
    private String stageType;
    private String stageStatus;
    private Integer orderIndex;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Task stats
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Double progressPercent;

    // Issue stats
    private Long totalIssues;
    private Long openIssues;
    private Long resolvedIssues;
}
