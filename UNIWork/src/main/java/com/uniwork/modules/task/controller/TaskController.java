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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/{taskId}/files")
    public ResponseEntity<?> getFiles(@PathVariable Long taskId) {
        List<FileAttachment> files = fileAttachmentService.findByTaskId(taskId);
        return ResponseFactory.success(files);
    }


    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long fileId) {
        return serveFile(fileId, false);
    }

    @GetMapping("/files/{fileId}/preview")
    public ResponseEntity<InputStreamResource> previewFile(@PathVariable Long fileId) {
        return serveFile(fileId, true);
    }

    private ResponseEntity<InputStreamResource> serveFile(Long fileId, boolean inline) {
        FileAttachment file = fileAttachmentService.findById(fileId);

        try {
            URL url = new URL(file.getFileUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);

            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Không tải được file từ Cloudinary, status = " + status);
            }

            long contentLength = conn.getContentLengthLong();
            InputStreamResource resource = new InputStreamResource(conn.getInputStream());

            String encodedFileName = URLEncoder.encode(file.getOriginalFileName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            String disposition = (inline ? "inline" : "attachment")
                    + "; filename=\"" + file.getOriginalFileName() + "\""
                    + "; filename*=UTF-8''" + encodedFileName;

            MediaType mediaType = file.getContentType() != null
                    ? MediaType.parseMediaType(file.getContentType())
                    : MediaType.APPLICATION_PDF;

            ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                    .contentType(mediaType);

            if (contentLength >= 0) {
                builder.contentLength(contentLength);
            }

            return builder.body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy file (id=" + fileId + "): " + e.getMessage(), e);
        }
    }

    @PostMapping("/{taskId}/status")
    public ResponseEntity updateTaskStatus(@PathVariable Long taskId,
                                      @RequestBody TaskRequest taskRequest,
                                      @RequestAttribute(required = false) Payload payload) {
        log.info("Updating task ID: {}", taskId);
        Task task = taskService.updateTaskStatus(payload.getUserId(), taskId, taskRequest);
        return ResponseFactory.success(task);
    }

    @GetMapping("/project/{projectId}/export")
    public ResponseEntity<byte[]> exportTasksToCsv(@PathVariable Long projectId) {
        log.info("Exporting tasks for project ID: {}", projectId);
        String csvContent = taskService.exportTasksToCsv(projectId);
        byte[] csvBytes = csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // Add BOM for Excel UTF-8 encoding
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] finalCsvBytes = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, finalCsvBytes, 0, bom.length);
        System.arraycopy(csvBytes, 0, finalCsvBytes, bom.length, csvBytes.length);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks_project_" + projectId + ".csv");
        headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(finalCsvBytes, headers, HttpStatus.OK);
    }
}
