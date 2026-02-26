package com.uniwork.service.impl;

import com.uniwork.model.entity.Project;
import com.uniwork.model.entity.Stage;
import com.uniwork.model.enumuration.ProjectMethod;
import com.uniwork.model.enumuration.StageStatus;
import com.uniwork.model.enumuration.StageType;
import com.uniwork.model.enumuration.TaskStatus;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.StageRepository;
import com.uniwork.repository.TaskRepository;
import com.uniwork.service.StageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    // =====================================================
    // CREATE STAGE
    // =====================================================

    @Override
    public Stage createStage(Long projectId, String name) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Stage> stages = stageRepository
                .findByProjectIdOrderByOrderIndexAsc(projectId);

        int nextOrder = stages.stream()
                .map(Stage::getOrderIndex)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        Stage stage = new Stage();
        stage.setProjectId(projectId);
        stage.setName(name);
        stage.setOrderIndex(nextOrder);
        stage.setStatus(StageStatus.PLANNED);
        stage.setActive(true);

        if (project.getMethod() == ProjectMethod.WATERFALL) {
            stage.setType(StageType.PHASE);
        } else {
            stage.setType(StageType.SPRINT);
        }

        return stageRepository.save(stage);
    }

    // =====================================================
    // ACTIVATE STAGE
    // =====================================================

    @Override
    public void activateStage(Long stageId) {

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        Project project = projectRepository.findById(stage.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getMethod() == ProjectMethod.WATERFALL) {
            validateWaterfallActivation(stage);
        }

        // Deactivate current active stage
        stageRepository.findByProjectIdAndStatus(
                stage.getProjectId(),
                StageStatus.ACTIVE
        ).ifPresent(active -> {
            active.setStatus(StageStatus.PLANNED);
            stageRepository.save(active);
        });

        stage.setStatus(StageStatus.ACTIVE);
        stageRepository.save(stage);
    }

    // =====================================================
    // COMPLETE STAGE
    // =====================================================

    @Override
    public void completeStage(Long stageId) {

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        if (stage.getStatus() != StageStatus.ACTIVE) {
            throw new RuntimeException("Only ACTIVE stage can be completed");
        }

        boolean hasUndoneTask = true;
//                taskRepository.existsByStageIdAndStatusNot(
//                        stageId,
//                        TaskStatus.DONE
//                );

        if (hasUndoneTask) {
            throw new RuntimeException(
                    "All tasks must be DONE before completing stage"
            );
        }

        stage.setStatus(StageStatus.COMPLETED);
        stageRepository.save(stage);
    }

    // =====================================================
    // GET STAGES
    // =====================================================

    @Override
    public List<Stage> getStagesByProject(Long projectId) {
        return stageRepository.findByProjectIdOrderByOrderIndexAsc(projectId);
    }

    // =====================================================
    // PRIVATE VALIDATION LOGIC
    // =====================================================

    private void validateWaterfallActivation(Stage stage) {

        List<Stage> stages = stageRepository
                .findByProjectIdOrderByOrderIndexAsc(stage.getProjectId());

        for (Stage s : stages) {
            if (s.getOrderIndex() < stage.getOrderIndex()
                    && s.getStatus() != StageStatus.COMPLETED) {
                throw new RuntimeException(
                        "Previous phase must be completed first"
                );
            }
        }
    }
}
