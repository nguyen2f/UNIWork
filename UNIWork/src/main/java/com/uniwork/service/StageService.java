package com.uniwork.service;

import com.uniwork.model.entity.Stage;

import java.util.List;

public interface StageService {

    Stage createStage(Long projectId, String name);

    void activateStage(Long stageId);

    void completeStage(Long stageId);

    List<Stage> getStagesByProject(Long projectId);
}
