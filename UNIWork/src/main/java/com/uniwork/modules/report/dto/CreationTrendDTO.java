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
public class CreationTrendDTO {
    private String granularity; // "DAILY", "MONTHLY", "YEARLY"
    private String from;
    private String to;
    private Long totalTasksCreated;
    private Long totalIssuesCreated;
    private List<TimeSeriesDataPoint> dataPoints;
}
