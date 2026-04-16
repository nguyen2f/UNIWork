package com.uniwork.modules.stage.request;

import lombok.Data;

import java.util.List;

@Data
public class MoveTasksRequest {
    private List<Long> taskIds;
    private Long targetStageId;
}
