package com.uniwork.modules.task.request;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskRequest {
    private Long projectId;
    private Long stageId; // Required — stage this task belongs to
    private List<Long> assignedTo;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private LocalDateTime dueDate;
    private String tags;
}
