package com.uniwork.dto;


import lombok.Data;

import java.util.Date;

@Data
public class TaskRequest {
    private Long projectId;
    private String title;
    private String description;
    private String priority; // e.g., Low, Medium, High
    private String status; // e.g., Not Started, In Progress, Completed
    private Date dueDate;
    private Date createdDate;
    private Date updatedDate;
}
