package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberKpiDTO {
    private Long userId;
    private String userName;
    private Integer totalCompletedTasks;
    private Double totalHoursSpent;
    private Double kpiScore;
}
