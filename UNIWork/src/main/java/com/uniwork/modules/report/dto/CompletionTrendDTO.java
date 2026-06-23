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
public class CompletionTrendDTO {
    private String granularity; // "DAILY", "MONTHLY", "YEARLY"
    private String from;
    private String to;
    private Long totalTasksCompleted;
    private Long totalIssuesCompleted;
    private List<TimeSeriesDataPoint> dataPoints;
}
