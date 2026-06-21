package com.uniwork.modules.task.service;

import com.uniwork.enums.*;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.modules.issue.entity.Issue;
import com.uniwork.modules.issue.repository.IssueRepository;
import com.uniwork.modules.task.dto.TaskDTO;
import com.uniwork.modules.task.dto.TaskDetailDTO;
import com.uniwork.modules.comment.dto.CommentDTO;
import com.uniwork.modules.file.dto.FileAttachmentDTO;
import com.uniwork.modules.notification.dto.NotificationDTO;
import com.uniwork.modules.project.entity.Project;
import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.projection.TaskDetailProjection;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.modules.task.request.TaskRequest;
import com.uniwork.modules.stage.repository.StageRepository;
import com.uniwork.modules.task.repository.TaskRepository;
import com.uniwork.modules.project.service.ProjectService;
import com.uniwork.modules.file.service.FileAttachmentService;
import com.uniwork.modules.notification.service.NotificationService;
import com.uniwork.modules.comment.service.CommentService;
import com.uniwork.modules.issue.service.IssueService;
import com.uniwork.modules.issue.dto.IssueDTO;
import com.uniwork.common.utils.BeanCopyUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ProjectService projectService;

    /**
     * @param userId
     * @param taskId
     * @param taskRequest
     * @return
     */
    @Override
    public Task updateTaskStatus(Long userId, Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CoreException(ErrorCode.TASK_NOT_FOUND, "Task not found"));

        if (!checkIssuesDone(taskId)) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Not all issue have been done, so you cannot change the status");
        }
        if (taskRequest.getStatus() != null &&
                (taskRequest.getStatus() == TaskStatus.COMPLETED.getCode() || taskRequest.getStatus() == TaskStatus.REVIEWING.getCode())) {
            task.setCompleted(true);
        }
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);

        if (taskRequest.getStatus() != null) {
            task.setStatus(TaskStatus.fromCode(taskRequest.getStatus()));
        }

        BeanCopyUtils.copyNonNullProperties(taskRequest, task, "taskId", "createdBy", "createdDate", "projectId", "stageId", "isDeleted", "status");

        Task updatedTask = taskRepository.save(task);
        return updatedTask;
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueRepository issueRepository;

    public List<TaskDTO> getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Can not find this project");
        }

        return taskRepository
                .findByProjectIdWithUser(projectId)
                .stream()
                .map(TaskDTO::new)
                .toList();
    }

    public TaskDetailDTO getTaskById(Long userId, Long taskId) {
        TaskDetailDTO dto = new TaskDetailDTO();
        TaskDetailProjection task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new CoreException(ErrorCode.TASK_NOT_FOUND, "Task not found");
        }
        TaskDTO taskDTO = new TaskDTO(task);
        dto.setTask(taskDTO);

        List<FileAttachmentDTO> attachments = fileAttachmentService.getAllFileAttachment(taskId);
        dto.setFileAttachments(attachments);

//        if (true) {
//
//            List<TaskDetailProjection> taskDetailProjectionList =
//                    taskRepository.findByParentId(taskDTO.getTaskId());
//
//            List<TaskDTO> childTasks = taskDetailProjectionList
//                    .stream()
//                    .map(TaskDTO::new)
//                    .toList();
//
//            dto.setChildTasks(childTasks);
//        }

        List<CommentDTO> comments = commentService.getAllCommentDTO(taskId);
        dto.setComments(comments);

        // Load issues for this task
        List<IssueDTO> issues = issueService.getIssuesByTaskId(taskId);
        dto.setIssues(issues);

        return dto;
    }

    @Transactional
    public Task createTask(Long userId, TaskRequest taskRequest) {

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
        parentTask.setTitle(taskRequest.getTitle());
        parentTask.setDescription(taskRequest.getDescription());
        parentTask.setPriority(Priority.fromCode(taskRequest.getPriority()));
        parentTask.setStatus(TaskStatus.PENDING);
        parentTask.setDueDate(taskRequest.getDueDate());
        parentTask.setCreatedDate(LocalDateTime.now().withNano(0));
        parentTask.setCompleted(false);
        parentTask.setTags(taskRequest.getTags());

        Task savedParentTask = taskRepository.save(parentTask);

        List<Issue> childTasks = new ArrayList<>();

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .type(NotificationType.TASK_ASSIGNED)
                .entityType(NotificationEntityType.TASK)
                .createdAt(LocalDateTime.now())
                .build();

        for (Long memberId : memberIds) {

            if (!projectService.checkProjectMember(project.getProjectId(), memberId)) {
                AssignMemberRequest assignMemberRequest = new AssignMemberRequest();
                assignMemberRequest.setUserId(memberId);
                assignMemberRequest.setRole(Role.MEMBER.toString());
                projectService.assignMember(project.getProjectId(), assignMemberRequest);
            }

            Issue childTask = new Issue();
            childTask.setProjectId(project.getProjectId());
            childTask.setStageId(stageId);
            childTask.setTaskId(savedParentTask.getTaskId());
            childTask.setAssignedTo(memberId);
            childTask.setReportedBy(userId);
            childTask.setTitle(taskRequest.getTitle());
            childTask.setDescription(taskRequest.getDescription());
            childTask.setPriority(Priority.fromCode(taskRequest.getPriority()));
            childTask.setStatus(IssueStatus.OPEN);
            childTask.setDueDate(taskRequest.getDueDate());
            childTask.setCreatedDate(LocalDateTime.now().withNano(0));
//            childTask.set(false);
//            childTask.setTags(taskRequest.getTags());

            childTasks.add(childTask);

            notificationService.sendNotification(memberId, notificationDTO);
        }

        issueRepository.saveAll(childTasks);

        return parentTask;
    }

    public Task updateTask(Long userId, Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CoreException(ErrorCode.TASK_NOT_FOUND, "Task not found"));

        if (!checkIssuesDone(taskId) && taskRequest.getStatus() != task.getStatus().getCode()) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "Not all issues have been done, so you cannot change the status");
        }
        if (taskRequest.getStatus() != null &&
                (taskRequest.getStatus() == TaskStatus.COMPLETED.getCode() || taskRequest.getStatus() == TaskStatus.REVIEWING.getCode())) {
            task.setCompleted(true);
        }
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);
        if (taskRequest.getAssignedTo() != null && !taskRequest.getAssignedTo().isEmpty()) {
            task.setAssignedTo(taskRequest.getAssignedTo().get(0));
        }
        task.setManagedBy(userId);

        if (taskRequest.getStatus() != null) {
            task.setStatus(TaskStatus.fromCode(taskRequest.getStatus()));
        }

        BeanCopyUtils.copyNonNullProperties(taskRequest, task, "taskId", "createdBy", "createdDate", "projectId", "stageId", "isDeleted", "status");

        Task updatedTask = taskRepository.save(task);
        return updatedTask;
    }

    public Task deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CoreException(ErrorCode.TASK_NOT_FOUND, "Can not find this task"));

        // Soft delete — set isDeleted = true instead of removing from DB
        task.setIsDeleted(true);
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);
        taskRepository.save(task);

        // Also soft-delete child tasks if this is a parent task
        List<Issue> childIssues = issueRepository.findAllByTaskId(taskId);
        for (Issue child : childIssues) {
            child.setIsDeleted(true);
            child.setUpdatedDate(LocalDateTime.now());
            child.setUpdatedBy(userId);
        }
        issueRepository.saveAll(childIssues);

        return task;
    }

    @Override
    public List<TaskDTO> getTasksByStageId(Long stageId) {
        List<TaskDetailProjection> projections = taskRepository.findByStageIdWithUser(stageId);
        return projections.stream().map(TaskDTO::new).toList();
    }

    @Override
    public List<TaskDTO> getTasksByAssignedTo(Long assignedTo, Integer priority, Integer status) {
        List<TaskDetailProjection> projections = taskRepository.findByAssignedToWithUser(
                assignedTo,
                priority != null ? Priority.fromCode(priority) : null,
                status != null ? TaskStatus.fromCode(status) : null
        );
        return projections.stream().map(TaskDTO::new).toList();
    }

    private Boolean checkIssuesDone(Long taskId) {
        List<Issue> issues = issueRepository.findAllByTaskId(taskId);
        return issues.stream()
                       .allMatch(issue -> issue.getStatus() == IssueStatus.CLOSED);
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
