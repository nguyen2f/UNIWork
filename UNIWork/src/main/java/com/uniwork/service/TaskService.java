package com.uniwork.service;


import com.uniwork.dto.TaskRequest;
import com.uniwork.model.Project;
import com.uniwork.model.Task;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;

    public ResponseEntity getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        return ResponseEntity.ok(taskRepository.findAllByProjectId(projectId));
    }

    public ResponseEntity getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }
        return ResponseEntity.ok(task);
    }

    public ResponseEntity createTask(TaskRequest taskRequest) {
        Project project = projectService.getProjectById(taskRequest.getProjectId());
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setProjectId(project.getProjectId());
        task.setCreatedDate(new Date());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    public ResponseEntity updateTask(TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskRequest.getId()).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    public ResponseEntity deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }
        taskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully");
    }


}
