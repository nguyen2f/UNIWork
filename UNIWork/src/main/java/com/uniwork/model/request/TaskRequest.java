package com.uniwork.model.request;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskRequest {
    private Long taskId;
    private Long projectId;
    private List<Long> assignedTo;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String tags;
}
