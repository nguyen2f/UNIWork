package com.uniwork.controller;

import com.uniwork.entity.dto.TaskDetailDTO;
import com.uniwork.entity.model.FileAttachment;
import com.uniwork.entity.model.Task;
import com.uniwork.entity.request.TaskRequest;
import com.uniwork.entity.request.UploadFileAttachmentRequest;
import com.uniwork.entity.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.FileAttachmentService;
import com.uniwork.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private FileAttachmentService fileAttachmentService;

    @GetMapping("/all/{projectId}")
    public ResponseEntity getTasksByProjectId(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching tasks for project ID: {}", projectId);
        List<Task> tasks = taskService.getAllTasksByProjectId(projectId);
        return ResponseFactory.success(tasks);
    }

    @GetMapping("/detail/{projectId}/{taskId}")
    public ResponseEntity getTaskById(@PathVariable Long taskId, @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching task with ID: {}", taskId);
        TaskDetailDTO taskDetailDTO = taskService.getTaskById(payload.getUserId(), taskId);
        return ResponseFactory.success(taskDetailDTO);
    }

    @PostMapping("/create")
    public ResponseEntity createTask(@RequestBody TaskRequest taskRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Creating task with request: {} for project ID: {}", taskRequest);
        List<Task> tasks = taskService.createTask(payload.getUserId(), taskRequest);
        return ResponseFactory.success(tasks);
    }

    @PostMapping("/{projectId}/{taskId}/update")
    public ResponseEntity updateTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @PathVariable Long taskId, @RequestAttribute(required = false) Payload payload) {
        log.info("Updating task with request: {} for project IzD: {}", taskRequest);
        Task task = taskService.updateTask(payload.getUserId(), taskId, taskRequest);
        return ResponseFactory.success(task);
    }

    @DeleteMapping("/{projectId}/{taskId}/delete")
    public ResponseEntity deleteTask(@PathVariable Long taskId, @PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting task with ID: {} for project ID: {}", taskId, projectId);
        Task task = taskService.deleteTask(payload.getUserId(), taskId);
        return ResponseFactory.success(task);
    }

    @PostMapping("/{projectId}/{taskId}/file/upload")
    public ResponseEntity uploadFileAttachment(@RequestAttribute(required = false) Payload payload,
                                               @PathVariable Long projectId,
                                               @PathVariable Long taskId,
                                               @RequestBody UploadFileAttachmentRequest uploadFileAttachmentRequest) {
        log.info("Uploading file with ID: {} for task ID for project ID: {}", taskId, projectId);
        FileAttachment fileAttachment = fileAttachmentService.uploadFileAttachment(payload.getUserId(), taskId, uploadFileAttachmentRequest);
        return ResponseFactory.success(fileAttachment);
    }

    @Cacheable(value = "uniwork:task:assignedTo", key = "'userId:' +  #payload.getUserId()")
    @GetMapping("/all")
    public ResponseEntity getAllTasksByAssignedTo(@RequestAttribute(required = false) Payload payload,
                                                  @RequestParam(required = false) Integer priority,
                                                  @RequestParam(required = false) Integer status) {
        log.info("Fetching all tasks for assigned user: {}", payload.getUserId());
        List<Task> tasks = taskService.getAllTasksByAssignedTo(payload.getUserId(), priority, status);
        return ResponseFactory.success(tasks);
    }
}
