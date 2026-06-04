package com.uniwork.modules.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueItemDTO {
    private Long id;
    private String type; // "TASK" or "ISSUE"
    private String title;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private Long daysOverdue;
    private String assigneeName;
    private Long assignedTo;
    private Long projectId;
    private String projectName;
}
