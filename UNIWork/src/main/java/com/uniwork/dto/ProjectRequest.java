package com.uniwork.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectRequest {
    private String name;
    private String description;
    private String status; // e.g., "active", "completed", "archived"
    private Date startDate; // ISO 8601 format
    private Date endDate; // ISO 8601 format
}

