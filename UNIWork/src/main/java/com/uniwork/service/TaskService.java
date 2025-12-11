package com.uniwork.service;


import com.uniwork.model.dto.TaskDetailDTO;
import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.enumuration.TaskStatus;
import com.uniwork.model.entity.Comment;
import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.request.AssignMemberRequest;
import com.uniwork.model.request.TaskRequest;
import com.uniwork.model.entity.Project;
import com.uniwork.model.entity.Task;
import com.uniwork.exceptions.CoreException;
import com.uniwork.exceptions.ErrorCode;
import com.uniwork.repository.TaskRepository;
import com.uniwork.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FileAttachmentService fileAttachmentService;
    @Autowired
    private UserService userService;

    public List<Task> getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new CoreException(ErrorCode.INTERNAL_ERROR, "");
        }
        return taskRepository.findAllByProjectId(projectId);
    }

    public TaskDetailDTO getTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        List<Comment> comments = commentService.getAllComment(taskId);
        List<FileAttachment> attachments = fileAttachmentService.getAllFileAttachment(taskId);
        TaskDetailDTO taskDetailDTO = new TaskDetailDTO(task, comments, attachments);
        return taskDetailDTO;
    }

    public List<Task> createTask(Long userId, TaskRequest taskRequest) {
        Project project = projectService.getProjectById(taskRequest.getProjectId());
        List<Task> tasks = new ArrayList<>();
        List<Long> memberIds = taskRequest.getAssignedTo();

        for (Long memberId : memberIds) {

            //check đã lưu user vào project member chưa, nếu chưa thì phải thêm mới
            Boolean checkMemberInProject = projectService.checkProjectMember(taskRequest.getProjectId(), memberId);
            if (!checkMemberInProject) {
                AssignMemberRequest assignMemberRequest = new AssignMemberRequest();
                assignMemberRequest.setProjectId(taskRequest.getProjectId());
                assignMemberRequest.setUserId(memberId);
                assignMemberRequest.setRole("MEMBER");
                userService.assignMemberToProject(assignMemberRequest);
            }

            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setAssignedTo(memberId);
            task.setDescription(taskRequest.getDescription());
            task.setCreatedBy(userId);
            task.setProjectId(project.getProjectId());
            task.setCreatedDate(LocalDateTime.now().withNano(0));
            task.setStatus(TaskStatus.fromCode(taskRequest.getStatus()));
            task.setDueDate(taskRequest.getDueDate());
            task.setPriority(Priority.fromCode(taskRequest.getPriority()));
            task.setTags(taskRequest.getTags());

            tasks.add(task);
        }

        List<Task> savedTasks = taskRepository.saveAll(tasks);
        return savedTasks;
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

        BeanCopyUtils.copyNonNullProperties(taskRequest, task,
                "taskId", "createdBy", "createdDate", "projectId");

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
