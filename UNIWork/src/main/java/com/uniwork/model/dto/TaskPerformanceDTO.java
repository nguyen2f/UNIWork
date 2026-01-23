package com.uniwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskPerformanceDTO {
    private Long userId;
    private Long totalTasks;
    private Long doneTasks;
    private Long remainingTasks;
    private Double performance;
}
