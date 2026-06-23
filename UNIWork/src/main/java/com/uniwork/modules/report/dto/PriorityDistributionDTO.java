package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriorityDistributionDTO {
    private String priority;
    private Long count;
    private Double percentage;
}
