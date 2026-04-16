package com.uniwork.modules.task.controller;

import com.uniwork.modules.task.dto.TaskDTO;
import com.uniwork.modules.task.dto.TaskDetailDTO;
import com.uniwork.modules.file.entity.FileAttachment;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.request.TaskRequest;
import com.uniwork.modules.file.request.UploadFileAttachmentRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.file.service.FileAttachmentServiceImpl;
import com.uniwork.modules.task.service.TaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/task")
@PreAuthorize("hasAuthority('PERM_MANAGE_TASKS')")
public class TaskController {

    @Autowired
    private TaskServiceImpl taskServiceImpl;
    @Autowired
    private FileAttachmentServiceImpl fileAttachmentService;

    @GetMapping("/all/{projectId}")
    public ResponseEntity getTasksByProjectId(@PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching tasks for project ID: {}", projectId);
        List<TaskDTO> tasks = taskServiceImpl.getAllTasksByProjectId(projectId);
        return ResponseFactory.success(tasks);
    }

    @GetMapping("/detail/{projectId}/{taskId}")
    public ResponseEntity getTaskById(@PathVariable Long taskId, @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching task with ID: {}", taskId);
        TaskDetailDTO taskDetailDTO = taskServiceImpl.getTaskById(payload.getUserId(), taskId);
        return ResponseFactory.success(taskDetailDTO);
    }

    @PostMapping("/create")
    public ResponseEntity createTask(@RequestBody TaskRequest taskRequest, @RequestAttribute(required = false) Payload payload) {
        log.info("Creating task with request: {} for project ID: {}", taskRequest);
        List<Task> tasks = taskServiceImpl.createTask(payload.getUserId(), taskRequest);
        return ResponseFactory.success(tasks);
    }

    @PostMapping("/{projectId}/{taskId}/update")
    public ResponseEntity updateTask(@RequestBody TaskRequest taskRequest, @PathVariable Long projectId, @PathVariable Long taskId, @RequestAttribute(required = false) Payload payload) {
        log.info("Updating task with request: {} for project ID: {}", taskRequest);
        Task task = taskServiceImpl.updateTask(payload.getUserId(), taskId, taskRequest);
        return ResponseFactory.success(task);
    }

    @PreAuthorize("hasAuthority('PERM_DELETE_TASK')")
    @DeleteMapping("/{projectId}/{taskId}/delete")
    public ResponseEntity deleteTask(@PathVariable Long taskId, @PathVariable Long projectId, @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting task with ID: {} for project ID: {}", taskId, projectId);
        Task task = taskServiceImpl.deleteTask(payload.getUserId(), taskId);
        return ResponseFactory.success(task);
    }

    @PostMapping("/{projectId}/{taskId}/file/upload")
    public ResponseEntity uploadFileAttachment(@RequestAttribute(required = false) Payload payload,
                                               @PathVariable Long projectId,
                                               @PathVariable Long taskId,
                                               @RequestParam("file") MultipartFile file) {
        log.info("Uploading file with ID: {} for task ID for project ID: {}", taskId, projectId);
        FileAttachment fileAttachment = fileAttachmentService.uploadFileAttachment(payload.getUserId(), taskId, file);
        return ResponseFactory.success(fileAttachment);
    }

    @GetMapping("/all")
    public ResponseEntity getAllTasksByAssignedTo(@RequestAttribute(required = false) Payload payload,
                                                  @RequestParam(required = false) Integer priority,
                                                  @RequestParam(required = false) Integer status) {
        log.info("Fetching all tasks for assigned user: {}", payload.getUserId());
        List<Task> tasks = taskServiceImpl.getAllTasksByAssignedTo(payload.getUserId(), priority, status);
        return ResponseFactory.success(tasks);
    }
}
