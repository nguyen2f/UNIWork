package com.uniwork.entity.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ProjectRequest {
    private Long projectId;
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer priority;
    private String category;
    private String client;
    private String department;
    private String riskLevel;
}

