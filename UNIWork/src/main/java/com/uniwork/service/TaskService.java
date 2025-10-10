package com.uniwork.service;


import com.uniwork.entity.dto.TaskDetailDTO;
import com.uniwork.entity.model.Comment;
import com.uniwork.entity.request.TaskRequest;
import com.uniwork.entity.model.Project;
import com.uniwork.entity.model.Task;
import com.uniwork.repository.TaskRepository;
import com.uniwork.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentService commentService;

    public ResponseEntity getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        return ResponseEntity.ok(taskRepository.findAllByProjectId(projectId));
    }

    public ResponseEntity getTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (userId != task.getAssignedTo()) {
            return ResponseEntity.status(403).body("You are not the assignee of this task");
        }
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }

        List<Comment> comments = commentService.getAllComment(taskId);
        TaskDetailDTO taskDetailDTO = new TaskDetailDTO(task, comments);
        return ResponseEntity.ok(taskDetailDTO);
    }

    public ResponseEntity<List<Task>> createTask(Long userId, TaskRequest taskRequest) {
        Project project = projectService.getProjectById(taskRequest.getProjectId());
        List<Task> tasks = new ArrayList<>();
        List<Long> memberIds = taskRequest.getAssignedTo();

        for (Long memberId : memberIds) {
            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setAssignedTo(memberId);
            task.setDescription(taskRequest.getDescription());
            task.setCreatedBy(userId);
            task.setProjectId(project.getProjectId());
            task.setCreatedDate(new Date());
            task.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : "NEW");
            task.setDueDate(taskRequest.getDueDate());
            task.setPriority(taskRequest.getPriority());
            task.setTags(taskRequest.getTags());

            tasks.add(task);
        }

        List<Task> savedTasks = taskRepository.saveAll(tasks);
        return ResponseEntity.ok(savedTasks);
    }

    public ResponseEntity<?> updateTask(Long userId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskRequest.getTaskId()).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }

        task.setUpdatedDate(new Date());
        task.setUpdateBy(userId);

        BeanCopyUtils.copyNonNullProperties(taskRequest, task,
                "taskId", "createdBy", "createdDate", "projectId");

        Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }


    public ResponseEntity deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }
        if (task.getAssignedTo() != userId) {
            return ResponseEntity.status(403).body("You are not the assignee of this task");
        }
        taskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully");
    }

}
