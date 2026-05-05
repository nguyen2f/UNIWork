package com.uniwork.modules.issue.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueRequest {
    private Long taskId;
    private Long projectId;
    private Long assignedTo;
    private String title;
    private String description;
    private Integer type;       // IssueType code
    private Integer priority;   // Priority code
    private Integer status;     // IssueStatus code
    private LocalDateTime dueDate;
}
