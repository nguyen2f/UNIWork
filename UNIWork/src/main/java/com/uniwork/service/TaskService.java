package com.uniwork.service;


import com.uniwork.dto.TaskRequest;
import com.uniwork.model.Project;
import com.uniwork.model.Task;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(TaskRequest taskRequest) {
        Project project = projectService.getProjectById(taskRequest.getProjectId());
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setProjectId(project.getId());
        task.setCreatedDate(new Date());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        return taskRepository.save(task);
    }
}
