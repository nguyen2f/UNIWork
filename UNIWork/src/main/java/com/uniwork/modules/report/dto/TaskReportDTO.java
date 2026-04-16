package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReportDTO {
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Double completedPercent;
}
