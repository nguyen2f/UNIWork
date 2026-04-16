package com.uniwork.modules.file.service;

import com.uniwork.modules.file.dto.FileAttachmentDTO;
import com.uniwork.modules.file.entity.FileAttachment;
import com.uniwork.modules.file.request.UploadFileAttachmentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileAttachmentService {

    FileAttachment uploadFileAttachment(Long userId, Long taskId, MultipartFile file);

    List<FileAttachmentDTO> getAllFileAttachment(Long taskId);

    Map uploadImage(MultipartFile file, String publicId);

    Map uploadFile(MultipartFile file, String publicId);
}
