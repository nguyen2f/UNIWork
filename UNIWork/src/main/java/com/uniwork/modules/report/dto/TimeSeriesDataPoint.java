package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSeriesDataPoint {
    private String label;    // e.g., "2026-06-22", "2026-06", "2026"
    private Long tasks;
    private Long issues;
}
