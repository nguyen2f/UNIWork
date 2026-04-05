package com.uniwork.service;

import com.uniwork.model.dto.StageDetailDTO;
import com.uniwork.model.entity.Stage;
import com.uniwork.model.enumuration.ProjectMethod;
import com.uniwork.model.request.MoveTasksRequest;
import com.uniwork.model.request.StageRequest;

import java.util.List;

public interface StageService {

    Stage createStage(Long projectId, StageRequest request);

    Stage updateStage(Long stageId, StageRequest request);

    void activateStage(Long stageId);

    void completeStage(Long stageId);

    List<Stage> getStagesByProject(Long projectId);

    StageDetailDTO getStageDetail(Long stageId);

    void moveTasksToStage(MoveTasksRequest request);

    List<Stage> initDefaultStages(Long projectId, ProjectMethod method);
}
