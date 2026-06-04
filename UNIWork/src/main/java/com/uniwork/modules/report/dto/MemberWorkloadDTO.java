package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberWorkloadDTO {
    private Long userId;
    private String userName;
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Long totalIssues;
    private Long completedIssues;
    private Long pendingIssues;
    private Double taskCompletionRate;
}
