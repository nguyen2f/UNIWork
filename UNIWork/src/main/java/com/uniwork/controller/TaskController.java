package com.uniwork.controller;

import com.uniwork.dto.TaskRequest;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity getTasksByProjectId(@PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Fetching tasks for project ID: {}", projectId);
        return taskService.getAllTasksByProjectId(projectId);
    }

    @GetMapping("/{projectId}/task/{taskId}")
    public ResponseEntity getTaskById(@PathVariable Long taskId, @RequestAttribute Payload payload) {
        log.info("Fetching task with ID: {}", taskId);
        return taskService.getTaskById(taskId);
    }

    @PostMapping("/{projectId}/create")
    public ResponseEntity createTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Creating task with request: {} for project ID: {}", taskRequest, projectId);
        return taskService.createTask(taskRequest);
    }

    @PostMapping("/{projectId}/update")
    public ResponseEntity updateTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Updating task with request: {} for project ID: {}", taskRequest, projectId);
        return taskService.updateTask(taskRequest);
    }

    @DeleteMapping("/{projectId}/delete/{taskId}")
    public ResponseEntity deleteTask(@PathVariable Long taskId, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Deleting task with ID: {} for project ID: {}", taskId, projectId);
        return taskService.deleteTask(taskId);
    }
}
