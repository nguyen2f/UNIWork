package com.uniwork.service.impl;

import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.model.dto.NotificationDTO;
import com.uniwork.model.dto.TaskDTO;
import com.uniwork.model.dto.TaskDetailDTO;
import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.entity.Project;
import com.uniwork.model.entity.Task;
import com.uniwork.model.enumuration.*;
import com.uniwork.model.projection.TaskDetailProjection;
import com.uniwork.model.request.AssignMemberRequest;
import com.uniwork.model.request.TaskRequest;
import com.uniwork.repository.TaskRepository;
import com.uniwork.service.FileAttachmentService;
import com.uniwork.service.NotificationService;
import com.uniwork.service.TaskService;
import com.uniwork.service.UserService;
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

    public List<TaskDTO> getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
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

        List<FileAttachment> attachments = fileAttachmentService.getAllFileAttachment(taskId);
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
        return dto;
    }

    @Transactional
    public List<Task> createTask(Long userId, TaskRequest taskRequest) {

        Project project = projectService.getProjectById(taskRequest.getProjectId());
        List<Long> memberIds = taskRequest.getAssignedTo();


        Task parentTask = new Task();
        parentTask.setProjectId(project.getProjectId());
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
        if (task == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }

        if (taskRequest.getStatus() == TaskStatus.COMPLETED.getCode() || taskRequest.getStatus() == TaskStatus.REVIEWING.getCode()) {
            task.setCompleted(true);
        }
        task.setUpdatedDate(LocalDateTime.now());
        task.setUpdatedBy(userId);
        task.setStatus(TaskStatus.fromCode(taskRequest.getStatus()));

        BeanCopyUtils.copyNonNullProperties(taskRequest, task, "taskId", "createdBy", "createdDate", "projectId");

        Task updatedTask = taskRepository.save(task);
        return updatedTask;
    }


    public Task deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }
        if (task.getAssignedTo() != userId) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }
        taskRepository.delete(task);
        return task;
    }

    //    @Cacheable(value = "uniwork:task:assignedTo", key = "'userId:' +  #assignedTo")

    public List<Task> getAllTasksByAssignedTo(Long assignedTo, Integer priority, Integer status) {
        List<Task> tasks = taskRepository.findAllByAssignedToAndFilter(assignedTo, Priority.fromCode(priority), TaskStatus.fromCode(status));
        return tasks;
    }

}
