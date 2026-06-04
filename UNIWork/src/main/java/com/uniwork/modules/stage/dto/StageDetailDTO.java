package com.uniwork.modules.stage.dto;

import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.modules.task.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDetailDTO {

    private Long stageId;
    private Long projectId;
    private String name;
    private String description;
    private String goal;
    private String type;
    private Integer orderIndex;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;

    // Task statistics
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long doingTasks;
    private Double progressPercent;

    // Issue statistics
    private Long totalIssues;
    private Long openIssues;
    private Long resolvedIssues;

    // Tasks in this stage
    private List<TaskDTO> tasks;

    public StageDetailDTO(Stage stage) {
        this.stageId = stage.getStageId();
        this.projectId = stage.getProjectId();
        this.name = stage.getName();
        this.description = stage.getDescription();
        this.goal = stage.getGoal();
        this.type = stage.getType() != null ? stage.getType().name() : null;
        this.orderIndex = stage.getOrderIndex();
        this.startDate = stage.getStartDate();
        this.endDate = stage.getEndDate();
        this.status = stage.getStatus() != null ? stage.getStatus().name() : null;
    }
}
