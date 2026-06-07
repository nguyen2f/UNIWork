package com.uniwork.modules.task.controller;

import com.uniwork.modules.task.dto.TaskDTO;
import com.uniwork.modules.task.dto.TaskDetailDTO;
import com.uniwork.modules.file.entity.FileAttachment;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.modules.task.request.TaskRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.file.service.FileAttachmentServiceImpl;
import com.uniwork.modules.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
@PreAuthorize("hasAuthority('PERM_MANAGE_TASKS')")
public class TaskController {

    @Autowired
    private FileAttachmentServiceImpl fileAttachmentService;
    @Autowired
    private TaskService taskService;

    // =====================================================
    // GET TASKS BY STAGE
    // =====================================================

    @GetMapping("/stage/{stageId}")
    public ResponseEntity getTasksByStageId(@PathVariable Long stageId,
                                             @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching tasks for stage ID: {}", stageId);
        List<TaskDTO> tasks = taskService.getTasksByStageId(stageId);
        return ResponseFactory.success(tasks);
    }

    // =====================================================
    // GET MY TASKS (tasks assigned to current user)
    // =====================================================

    @GetMapping("/my-tasks")
    public ResponseEntity getMyTasks(@RequestAttribute(required = false) Payload payload,
                                      @RequestParam(required = false) Integer priority,
                                      @RequestParam(required = false) Integer status) {
        log.info("Fetching tasks for user: {}", payload.getUserId());
        List<TaskDTO> tasks = taskService.getTasksByAssignedTo(payload.getUserId(), priority, status);
        return ResponseFactory.success(tasks);
    }

    // =====================================================
    // GET TASK DETAIL
    // =====================================================

    @GetMapping("/{taskId}")
    public ResponseEntity getTaskDetail(@PathVariable Long taskId,
                                         @RequestAttribute(required = false) Payload payload) {
        log.info("Fetching task detail for task ID: {}", taskId);
        TaskDetailDTO taskDetail = taskService.getTaskById(payload.getUserId(), taskId);
        return ResponseFactory.success(taskDetail);
    }

    // =====================================================
    // CREATE TASK
    // =====================================================

    @PostMapping
    public ResponseEntity createTask(@RequestBody TaskRequest taskRequest,
                                      @RequestAttribute(required = false) Payload payload) {
        log.info("Creating task in stage: {}", taskRequest.getStageId());
        Task task = taskService.createTask(payload.getUserId(), taskRequest);
        return ResponseFactory.success(task);
    }

    // =====================================================
    // UPDATE TASK
    // =====================================================

    @PutMapping("/{taskId}")
    public ResponseEntity updateTask(@PathVariable Long taskId,
                                      @RequestBody TaskRequest taskRequest,
                                      @RequestAttribute(required = false) Payload payload) {
        log.info("Updating task ID: {}", taskId);
        Task task = taskService.updateTask(payload.getUserId(), taskId, taskRequest);
        return ResponseFactory.success(task);
    }

    // =====================================================
    // DELETE TASK (soft delete)
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_DELETE_TASK')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTask(@PathVariable Long taskId,
                                      @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting task ID: {}", taskId);
        Task task = taskService.deleteTask(payload.getUserId(), taskId);
        return ResponseFactory.success(task);
    }

    // =====================================================
    // UPLOAD FILE ATTACHMENT
    // =====================================================

    @PostMapping("/{taskId}/files")
    public ResponseEntity uploadFileAttachment(@PathVariable Long taskId,
                                                @RequestAttribute(required = false) Payload payload,
                                                @RequestParam("file") MultipartFile file) {
        log.info("Uploading file for task ID: {}", taskId);
        FileAttachment fileAttachment = fileAttachmentService.uploadFileAttachment(payload.getUserId(), taskId, file);
        return ResponseFactory.success(fileAttachment);
    }

    @PostMapping("/{taskId}/status")
    public ResponseEntity updateTaskStatus(@PathVariable Long taskId,
                                      @RequestBody TaskRequest taskRequest,
                                      @RequestAttribute(required = false) Payload payload) {
        log.info("Updating task ID: {}", taskId);
        Task task = taskService.updateTaskStatus(payload.getUserId(), taskId, taskRequest);
        return ResponseFactory.success(task);
    }
}
