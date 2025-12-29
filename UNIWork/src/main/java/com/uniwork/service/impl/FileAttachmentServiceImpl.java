package com.uniwork.service.impl;

import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.request.UploadFileAttachmentRequest;
import com.uniwork.repository.FileAttachmentRepository;
import com.uniwork.service.FileAttachmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileAttachmentServiceImpl implements FileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;

    public FileAttachmentServiceImpl(FileAttachmentRepository fileAttachmentRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

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
