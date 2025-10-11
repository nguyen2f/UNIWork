package com.uniwork.controller;

import com.uniwork.entity.request.TaskRequest;
import com.uniwork.entity.request.UploadFileAttachmentRequest;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.FileAttachmentService;
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
    @Autowired
    private FileAttachmentService fileAttachmentService;

    @GetMapping("/{projectId}/get-all")
    public ResponseEntity getTasksByProjectId(@PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Fetching tasks for project ID: {}", projectId);
        return taskService.getAllTasksByProjectId(projectId);
    }

    @GetMapping("/{projectId}/get-detail/{taskId}")
    public ResponseEntity getTaskById(@PathVariable Long taskId, @RequestAttribute Payload payload) {
        log.info("Fetching task with ID: {}", taskId);
        return taskService.getTaskById(payload.getUserId(), taskId);
    }

    @PostMapping("/{projectId}/create-task")
    public ResponseEntity createTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Creating task with request: {} for project ID: {}", taskRequest, projectId);
        return taskService.createTask(payload.getUserId(), taskRequest);
    }

    @PostMapping("/{projectId}/update-task")
    public ResponseEntity updateTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Updating task with request: {} for project ID: {}", taskRequest, projectId);
        return taskService.updateTask(payload.getUserId(), taskRequest);
    }

    @DeleteMapping("/{projectId}/delete-task/{taskId}")
    public ResponseEntity deleteTask(@PathVariable Long taskId, @PathVariable Long projectId, @RequestAttribute Payload payload) {
        log.info("Deleting task with ID: {} for project ID: {}", taskId, projectId);
        return taskService.deleteTask(payload.getUserId(), taskId);
    }

    @PostMapping("/{projectId}/upload-file/{taskId}")
    public ResponseEntity uploadFileAttachment(@RequestAttribute Payload payload,
                                               @PathVariable Long projectId,
                                               @PathVariable Long taskId,
                                               @RequestBody UploadFileAttachmentRequest uploadFileAttachmentRequest) {
        log.info("Uploading file with ID: {} for task ID for project ID: {}", taskId, projectId);
        return fileAttachmentService.uploadFileAttachment(payload.getUserId(), taskId, uploadFileAttachmentRequest);
    }

    @GetMapping("/get-all-tasks")
    public ResponseEntity getAllTasksByAssignedTo(@RequestAttribute Payload payload,
                                                  @RequestParam(required = false) Integer priority,
                                                  @RequestParam(required = false) Integer status) {
        log.info("Fetching all tasks for assigned user: {}", payload.getUserId());
        return taskService.getAllTasksByAssignedTo(payload.getUserId(), priority, status);
    }
}
