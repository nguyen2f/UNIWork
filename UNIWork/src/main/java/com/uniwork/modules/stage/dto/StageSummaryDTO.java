package com.uniwork.modules.stage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageSummaryDTO {

    private Long stageId;
    private Long projectId;
    private String name;
    private String type;
    private Integer orderIndex;
    private String status;

    // Task statistics
    private Long totalTasks;
    private Long completedTasks;
    private Double progressPercent;
}
