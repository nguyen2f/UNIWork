package com.uniwork.service.impl;

import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.model.dto.*;
import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.entity.Project;
import com.uniwork.model.entity.Stage;
import com.uniwork.model.entity.Task;
import com.uniwork.model.enumuration.*;
import com.uniwork.model.projection.TaskDetailProjection;
import com.uniwork.model.request.AssignMemberRequest;
import com.uniwork.model.request.TaskRequest;
import com.uniwork.repository.StageRepository;
import com.uniwork.repository.TaskRepository;
import com.uniwork.service.*;
import com.uniwork.util.BeanCopyUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private StageRepository stageRepository;

    public List<TaskDTO> getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }
        List<TaskDetailProjection> tasks = taskRepository.findByProjectIdWithUser(projectId);

        List<TaskDTO> result = taskRepository
                .findByProjectIdWithUser(projectId)
                .stream()
                .map(TaskDTO::new)
                .toList();
        return result;
    }

    public TaskDetailDTO getTaskById(Long userId, Long taskId) {
        TaskDetailDTO dto = new TaskDetailDTO();
        TaskDetailProjection task = taskRepository.findByTaskId(taskId);
        TaskDTO taskDTO = new TaskDTO(task);
        dto.setTask(taskDTO);

        List<FileAttachmentDTO> attachments = fileAttachmentService.getAllFileAttachment(taskId);
        dto.setFileAttachments(attachments);

        if (taskDTO.getTaskParentId() == null) {
            List<TaskDetailProjection> taskDetailProjectionList =
                    taskRepository.findByParentId(taskDTO.getTaskId());

            List<TaskDTO> childTasks = taskDetailProjectionList
                    .stream()
                    .map(TaskDTO::new)
                    .toList();

            dto.setChildTasks(childTasks);
        }

        List<CommentDTO> comments = commentService.getAllCommentDTO(taskId);
        dto.setComments(comments);
        return dto;
    }

    @Transactional
    public List<Task> createTask(Long userId, TaskRequest taskRequest) {

        Project project = projectService.getProjectById(taskRequest.getProjectId());
        if (project == null) {
            throw new CoreException(ErrorCode.PROJECT_NOT_FOUND, "Can not find this project");
        }

        // Validate and resolve stageId
        Long stageId = resolveStageId(taskRequest.getStageId(), project);

        List<Long> memberIds = taskRequest.getAssignedTo();

        Task parentTask = new Task();
        parentTask.setProjectId(project.getProjectId());
        parentTask.setStageId(stageId);
        parentTask.setAssignedTo(null);
        parentTask.setCreatedBy(userId);
        parentTask.setParentId(null);
        parentTask.setTitle(taskRequest.getTitle());
        parentTask.setDescription(taskRequest.getDescription());
        parentTask.setPriority(Priority.fromCode(taskRequest.getPriority()));
        parentTask.setStatus(TaskStatus.PENDING);
        parentTask.setDueDate(taskRequest.getDueDate());
        parentTask.setCreatedDate(LocalDateTime.now().withNano(0));
        parentTask.setCompleted(false);
        parentTask.setTags(taskRequest.getTags());

        Task savedParentTask = taskRepository.save(parentTask);

        List<Task> childTasks = new ArrayList<>();


        NotificationDTO notificationDTO = NotificationDTO.builder()
                .type(NotificationType.TASK_ASSIGNED)
                .entityType(NotificationEntityType.TASK)
                .createdAt(LocalDateTime.now())
                .build();

        for (Long memberId : memberIds) {

            if (!projectService.checkProjectMember(project.getProjectId(), memberId)) {
                AssignMemberRequest assignMemberRequest = new AssignMemberRequest();
                assignMemberRequest.setProjectId(project.getProjectId());
                assignMemberRequest.setUserId(memberId);
                assignMemberRequest.setRole(Role.MEMBER.toString());
                userService.assignMemberToProject(assignMemberRequest);
            }

            Task childTask = new Task();
            childTask.setProjectId(project.getProjectId());
            childTask.setStageId(stageId);
            childTask.setAssignedTo(memberId);
            childTask.setCreatedBy(userId);
            childTask.setParentId(savedParentTask.getTaskId());
            childTask.setTitle(taskRequest.getTitle());
            childTask.setDescription(taskRequest.getDescription());
            childTask.setPriority(Priority.fromCode(taskRequest.getPriority()));
            childTask.setStatus(TaskStatus.PENDING);
            childTask.setDueDate(taskRequest.getDueDate());
            childTask.setCreatedDate(LocalDateTime.now().withNano(0));
            childTask.setCompleted(false);
            childTask.setTags(taskRequest.getTags());

            childTasks.add(childTask);

            notificationService.sendNotification(memberId, notificationDTO);
        }

        taskRepository.saveAll(childTasks);

        return childTasks;
    }

    public Task updateTask(Long userId, Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task.getParentId() == null && !checkSubTaskDone(taskId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Not all subtasks have been done, so you cannot change the status");
        }
        if (taskRequest.getStatus() == TaskStatus.COMPLETED.getCode() || taskRequest.getStatus() == TaskStatus.REVIEWING.getCode()) {
            task.setCompleted(true);
        }
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);
        task.setStatus(TaskStatus.fromCode(taskRequest.getStatus()));

        BeanCopyUtils.copyNonNullProperties(taskRequest, task, "taskId", "createdBy", "createdDate", "projectId", "stageId", "isDeleted");

        Task updatedTask = taskRepository.save(task);
        return updatedTask;
    }

    public Task deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new CoreException(ErrorCode.TASK_NOT_FOUND, "Can not find this task");
        }
        // Soft delete — set isDeleted = true instead of removing from DB
        task.setIsDeleted(true);
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);
        taskRepository.save(task);

        // Also soft-delete child tasks if this is a parent task
        if (task.getParentId() == null) {
            List<Task> childTasks = taskRepository.findByParentIdOrderByTaskIdDesc(taskId);
            for (Task child : childTasks) {
                child.setIsDeleted(true);
                child.setUpdatedDate(LocalDateTime.now());
                child.setUpdatedBy(userId);
            }
            taskRepository.saveAll(childTasks);
        }

        return task;
    }

    //    @Cacheable(value = "uniwork:task:assignedTo", key = "'userId:' +  #assignedTo")

    public List<Task> getAllTasksByAssignedTo(Long assignedTo, Integer priority, Integer status) {
        List<Task> tasks = taskRepository.findAllByAssignedToAndFilter(assignedTo, Priority.fromCode(priority), TaskStatus.fromCode(status));
        return tasks;
    }

    private Boolean checkSubTaskDone(Long taskId) {
        List<Task> subTasks = taskRepository.findByParentIdOrderByTaskIdDesc(taskId);
        return subTasks.stream().allMatch(Task::getCompleted);
    }

    /**
     * Resolve the stageId for a task:
     * - If stageId is provided, validate it belongs to the project and is in PLANNED or ACTIVE status
     * - If stageId is not provided, use the currently ACTIVE stage of the project
     */
    private Long resolveStageId(Long stageId, Project project) {
        if (stageId != null) {
            Stage stage = stageRepository.findById(stageId)
                    .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND));

            if (!stage.getProjectId().equals(project.getProjectId())) {
                throw new CoreException(ErrorCode.STAGE_INVALID_STATE, "Stage does not belong to this project");
            }

            // Only allow assigning tasks to PLANNED or ACTIVE stages
            if (stage.getStatus() == StageStatus.COMPLETED || stage.getStatus() == StageStatus.CANCELLED) {
                throw new CoreException(ErrorCode.STAGE_INVALID_STATE, "Cannot add tasks to a COMPLETED or CANCELLED stage");
            }

            return stageId;
        }

        // Default: use the currently ACTIVE stage
        return stageRepository.findByProjectIdAndStatus(project.getProjectId(), StageStatus.ACTIVE)
                .map(Stage::getStageId)
                .orElseThrow(() -> new CoreException(ErrorCode.STAGE_NOT_FOUND, "No active stage found for this project"));
    }
}
