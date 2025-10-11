package com.uniwork.entity.request;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectRequest {
    private Long projectId;
    private String name;
    private String description;
    private Integer status;
    private Date startDate;
    private Date endDate;
    private Integer priority;
    private String category;
    private String client;
    private String department;
    private String riskLevel;
}

