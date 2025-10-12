package com.uniwork.service;

import com.uniwork.entity.model.FileAttachment;
import com.uniwork.entity.request.UploadFileAttachmentRequest;
import com.uniwork.repository.FileAttachmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FileAttachmentService {


    private final FileAttachmentRepository fileAttachmentRepository;

    public FileAttachmentService(FileAttachmentRepository fileAttachmentRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    // Tạm thời upload file là gán 1 đường link, còn lưu vào server thì tính sau
    public FileAttachment uploadFileAttachment(Long userId, Long taskId, UploadFileAttachmentRequest uploadFileAttachmentRequest) {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setFileSize(uploadFileAttachmentRequest.getFileSize());
        fileAttachment.setFileName(uploadFileAttachmentRequest.getFileName());
        fileAttachment.setTaskId(taskId);
        fileAttachment.setUploaderId(userId);
        fileAttachment.setUploadDate(LocalDateTime.now());
        fileAttachment.setUrl(uploadFileAttachmentRequest.getUrl());
        fileAttachment.setFileType(uploadFileAttachmentRequest.getFileType());
        return fileAttachmentRepository.save(fileAttachment);
    }

    public List<FileAttachment> getAllFileAttachment(Long taskId) {
        return fileAttachmentRepository.findAllByTaskId(taskId);
    }
}
