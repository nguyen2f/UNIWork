package com.uniwork.modules.stage.service;

import com.uniwork.modules.stage.dto.StageDetailDTO;
import com.uniwork.modules.stage.dto.StageSummaryDTO;
import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.modules.stage.request.MoveTasksRequest;
import com.uniwork.modules.stage.request.StageRequest;

import java.util.List;

public interface StageService {

    Stage createStage(Long projectId, StageRequest request);

    Stage updateStage(Long stageId, StageRequest request);

    void deleteStage(Long stageId);

    void activateStage(Long stageId);

    void completeStage(Long stageId);

    List<Stage> getStagesByProject(Long projectId);

    List<StageSummaryDTO> getStagesSummary(Long projectId);

    StageDetailDTO getStageDetail(Long stageId);

    void moveTasksToStage(MoveTasksRequest request);

    List<Stage> initDefaultStages(Long projectId, ProjectMethod method);
}
