package com.uniwork.entity.request;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskRequest {
    private Long taskId;
    private Long projectId;
    private List<Long> assignedTo;
    private String title;
    private String description;
    private String priority; // e.g., Low, Medium, High
    private String status; // e.g., Not Started, In Progress, Completed
    private Date dueDate;
    private Date createdDate;
    private Date updatedDate;
    private String tags; // Comma-separated tags
}
