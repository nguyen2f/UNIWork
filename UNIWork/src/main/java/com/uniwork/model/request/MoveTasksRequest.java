package com.uniwork.model.request;

import lombok.Data;

import java.util.List;

@Data
public class MoveTasksRequest {
    private List<Long> taskIds;
    private Long targetStageId;
}
