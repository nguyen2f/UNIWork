package com.uniwork.service;

import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.request.UploadFileAttachmentRequest;

import java.util.List;

public interface FileAttachmentService {

    FileAttachment uploadFileAttachment(Long userId, Long taskId, UploadFileAttachmentRequest uploadFileAttachmentRequest);

    List<FileAttachment> getAllFileAttachment(Long taskId);
}
