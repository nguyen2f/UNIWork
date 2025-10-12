package com.uniwork.entity.request;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TaskRequest {
    private Long taskId;
    private Long projectId;
    private List<Long> assignedTo;
    private String title;
    private String description;
    private Integer priority; // e.g., Low, Medium, High
    private Integer status; // e.g., Not Started, In Progress, Completed
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String tags; // Comma-separated tags
}
