package com.uniwork.modules.project.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequest {
    private String name;
    private String description;
    private Integer priority;
    private String category;
    private String client;
    private Long departmentId;
    private String riskLevel;
    private String method; // AGILE, WATERFALL, or STANDARD (default)
    private Integer status; // PLANNING, IN_PROGRESS, COMPLETED, or ON_HOLD (default)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
