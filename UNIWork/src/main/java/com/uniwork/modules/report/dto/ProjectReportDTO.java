package com.uniwork.modules.report.dto;

import com.uniwork.modules.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReportDTO {
    private Project project;
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Double completedPercent;
    private Long countMember;
}
