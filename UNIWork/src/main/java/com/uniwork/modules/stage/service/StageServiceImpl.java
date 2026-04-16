package com.uniwork.modules.stage.service;

import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.modules.stage.dto.StageDetailDTO;
import com.uniwork.modules.task.dto.TaskDTO;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.enums.ProjectMethod;
import com.uniwork.enums.StageStatus;
import com.uniwork.enums.StageType;
import com.uniwork.enums.TaskStatus;
import com.uniwork.modules.task.projection.TaskDetailProjection;
import com.uniwork.modules.stage.request.MoveTasksRequest;
import com.uniwork.modules.stage.request.StageRequest;
import com.uniwork.modules.project.repository.ProjectRepository;
import com.uniwork.modules.stage.repository.StageRepository;
import com.uniwork.modules.task.repository.TaskRepository;
import com.uniwork.modules.stage.service.StageService;
import com.uniwork.common.utils.BeanCopyUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Stage createStage(Long projectId, StageRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CoreException(ErrorCode.PROJECT_NOT_FOUND));

        List<Stage> stages = stageRepository
                .findByProjectIdOrderByOrderIndexAsc(projectId);

        int nextOrder = stages.stream()
                .map(Stage::getOrderIndex)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        Stage stage = new Stage();
        stage.setProjectId(projectId);
        stage.setName(request.getName());
        stage.setOrderIndex(nextOrder);
        stage.setStartDate(request.getStartDate());
        stage.setEndDate(request.getEndDate());
        stage.setStatus(StageStatus.PLANNED);
        stage.setActive(true);
        stage.setIsDeleted(false);

        // Set type based on project methodology
        stage.setType(resolveStageType(project.getMethod()));

        return stageRepository.save(stage);
    }

    // =====================================================
    // UPDATE STAGE
    // =====================================================

    @Override
    public Stage updateStage(Long stageId, StageRequest request) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND));

        BeanCopyUtils.copyNonNullProperties(request, stage, "stageId", "projectId", "type", "orderIndex", "status", "active", "isDeleted");
        return stageRepository.save(stage);
    }

    // =====================================================
    // ACTIVATE STAGE
    // =====================================================

    @Override
    public void activateStage(Long stageId) {

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND));

        if (stage.getStatus() == StageStatus.COMPLETED || stage.getStatus() == StageStatus.CANCELLED) {
            throw new CoreException(ErrorCode.STAGE_INVALID_STATE, "Cannot activate a COMPLETED or CANCELLED stage");
        }

        Project project = projectRepository.findById(stage.getProjectId())
                .orElseThrow(() -> new CoreException(ErrorCode.PROJECT_NOT_FOUND));

        // Waterfall: must complete previous phases in order
        if (project.getMethod() == ProjectMethod.WATERFALL) {
            validateWaterfallActivation(stage);
        }

        // Deactivate current active stage (only one active at a time)
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
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND));

        if (stage.getStatus() != StageStatus.ACTIVE) {
            throw new CoreException(ErrorCode.STAGE_INVALID_STATE, "Only ACTIVE stage can be completed");
        }

        // Check that all tasks in this stage are COMPLETED
        boolean hasUndoneTasks = taskRepository.existsByStageIdAndStatusNot(stageId, TaskStatus.COMPLETED);

        if (hasUndoneTasks) {
            throw new CoreException(ErrorCode.STAGE_HAS_UNDONE_TASKS);
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
    // GET STAGE DETAIL (with tasks)
    // =====================================================

    @Override
    public StageDetailDTO getStageDetail(Long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND));

        StageDetailDTO dto = new StageDetailDTO(stage);

        // Task stats
        Long totalTasks = taskRepository.countByStageId(stageId);
        Long completedTasks = taskRepository.countByStageIdAndStatus(stageId, TaskStatus.COMPLETED);
        Long pendingTasks = taskRepository.countByStageIdAndStatus(stageId, TaskStatus.PENDING);
        Long doingTasks = taskRepository.countByStageIdAndStatus(stageId, TaskStatus.DOING);

        dto.setTotalTasks(totalTasks);
        dto.setCompletedTasks(completedTasks);
        dto.setPendingTasks(pendingTasks);
        dto.setDoingTasks(doingTasks);

        // Tasks list
        List<TaskDetailProjection> projections = taskRepository.findByStageIdWithUser(stageId);
        List<TaskDTO> tasks = projections.stream().map(TaskDTO::new).toList();
        dto.setTasks(tasks);

        return dto;
    }

    // =====================================================
    // MOVE TASKS TO STAGE
    // =====================================================

    @Override
    public void moveTasksToStage(MoveTasksRequest request) {

        Stage targetStage = stageRepository.findById(request.getTargetStageId())
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND, "Target stage not found"));

        if (targetStage.getStatus() == StageStatus.COMPLETED || targetStage.getStatus() == StageStatus.CANCELLED) {
            throw new CoreException(ErrorCode.STAGE_INVALID_STATE, "Cannot move tasks to a COMPLETED or CANCELLED stage");
        }

        List<Task> tasks = taskRepository.findAllById(request.getTaskIds());
        for (Task task : tasks) {
            // Verify task belongs to same project
            if (!task.getProjectId().equals(targetStage.getProjectId())) {
                throw new CoreException(ErrorCode.STAGE_INVALID_STATE,
                        "Task " + task.getTaskId() + " does not belong to the same project as the target stage");
            }
            task.setStageId(request.getTargetStageId());
        }
        taskRepository.saveAll(tasks);
    }

    // =====================================================
    // INIT DEFAULT STAGES
    // =====================================================

    @Override
    public List<Stage> initDefaultStages(Long projectId, ProjectMethod method) {
        List<Stage> stages = new ArrayList<>();

        if (method == ProjectMethod.WATERFALL) {
            String[] phaseNames = {
                    "Requirements",
                    "Design",
                    "Implementation",
                    "Testing",
                    "Deployment"
            };
            for (int i = 0; i < phaseNames.length; i++) {
                Stage stage = buildDefaultStage(projectId, phaseNames[i], StageType.PHASE, i + 1);
                // First phase starts as ACTIVE
                if (i == 0) {
                    stage.setStatus(StageStatus.ACTIVE);
                }
                stages.add(stage);
            }
        } else if (method == ProjectMethod.AGILE) {
            Stage sprint = buildDefaultStage(projectId, "Sprint 1", StageType.SPRINT, 1);
            sprint.setStatus(StageStatus.ACTIVE);
            stages.add(sprint);
        } else {
            // STANDARD — one default stage
            Stage defaultStage = buildDefaultStage(projectId, "General", StageType.DEFAULT, 1);
            defaultStage.setStatus(StageStatus.ACTIVE);
            stages.add(defaultStage);
        }

        return stageRepository.saveAll(stages);
    }

    // =====================================================
    // PRIVATE HELPERS
    // =====================================================

    private void validateWaterfallActivation(Stage stage) {
        List<Stage> stages = stageRepository
                .findByProjectIdOrderByOrderIndexAsc(stage.getProjectId());

        for (Stage s : stages) {
            if (s.getOrderIndex() < stage.getOrderIndex()
                    && s.getStatus() != StageStatus.COMPLETED) {
                throw new CoreException(ErrorCode.STAGE_WATERFALL_SEQUENCE,
                        "Phase '" + s.getName() + "' must be completed before activating '" + stage.getName() + "'");
            }
        }
    }

    private StageType resolveStageType(ProjectMethod method) {
        if (method == null) return StageType.DEFAULT;
        return switch (method) {
            case WATERFALL -> StageType.PHASE;
            case AGILE -> StageType.SPRINT;
            case STANDARD -> StageType.DEFAULT;
        };
    }

    private Stage buildDefaultStage(Long projectId, String name, StageType type, int orderIndex) {
        Stage stage = new Stage();
        stage.setProjectId(projectId);
        stage.setName(name);
        stage.setType(type);
        stage.setOrderIndex(orderIndex);
        stage.setStatus(StageStatus.PLANNED);
        stage.setActive(true);
        stage.setIsDeleted(false);
        return stage;
    }
}
