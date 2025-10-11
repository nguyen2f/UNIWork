package com.uniwork.service;

import com.uniwork.entity.model.FileAttachment;
import com.uniwork.entity.request.UploadFileAttachmentRequest;
import com.uniwork.repository.FileAttachmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FileAttachmentService {


    private final FileAttachmentRepository fileAttachmentRepository;

    public FileAttachmentService(FileAttachmentRepository fileAttachmentRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    // Tạm thời upload file là gán 1 đường link, còn lưu vào server thì tính sau
    public ResponseEntity uploadFileAttachment(Long userId, Long taskId, UploadFileAttachmentRequest uploadFileAttachmentRequest) {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setFileSize(uploadFileAttachmentRequest.getFileSize());
        fileAttachment.setFileName(uploadFileAttachmentRequest.getFileName());
        fileAttachment.setTaskId(taskId);
        fileAttachment.setUploaderId(userId);
        fileAttachment.setUploadDate(new Date());
        fileAttachment.setUrl(uploadFileAttachmentRequest.getUrl());
        fileAttachment.setFileType(uploadFileAttachmentRequest.getFileType());
        return ResponseEntity.ok(fileAttachmentRepository.save(fileAttachment));
    }
}
