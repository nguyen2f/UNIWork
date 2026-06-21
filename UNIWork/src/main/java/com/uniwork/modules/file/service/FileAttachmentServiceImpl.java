package com.uniwork.modules.file.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uniwork.modules.file.dto.FileAttachmentDTO;
import com.uniwork.modules.file.entity.FileAttachment;
import com.uniwork.modules.file.projection.FileAttachmentProjection;
import com.uniwork.modules.file.request.UploadFileAttachmentRequest;
import com.uniwork.modules.file.repository.FileAttachmentRepository;
import com.uniwork.modules.file.service.FileAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileAttachmentServiceImpl implements FileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private Cloudinary cloudinary;

    public FileAttachmentServiceImpl(FileAttachmentRepository fileAttachmentRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    public FileAttachment uploadFileAttachment(Long userId, Long taskId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String publicId = "attachments/task_" + taskId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        Map uploadResult = uploadFile(file, publicId);

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setTaskId(taskId);
        fileAttachment.setUploaderId(userId);
        fileAttachment.setUploadDate(LocalDateTime.now());

        fileAttachment.setPublicId((String) uploadResult.get("public_id"));
        fileAttachment.setFileUrl((String) uploadResult.get("secure_url"));
        fileAttachment.setFileSize(((Number) uploadResult.get("bytes")).longValue());
        fileAttachment.setContentType(file.getContentType());
        fileAttachment.setOriginalFileName(file.getOriginalFilename());

        return fileAttachmentRepository.save(fileAttachment);
    }

    public List<FileAttachmentDTO> getAllFileAttachment(Long taskId) {
        List<FileAttachmentProjection> projections = fileAttachmentRepository.findAllByTaskId(taskId);
        return projections.stream()
                .map(projection -> new FileAttachmentDTO(projection))
                .collect(Collectors.toList());    }

    // Upload IMAGE (avatar, ảnh)
    public Map uploadImage(MultipartFile file, String publicId) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "image"
            );

            return cloudinary.uploader().upload(file.getBytes(), options);

        } catch (IOException e) {
            throw new RuntimeException("Upload image failed", e);
        }
    }


    // Upload FILE (pdf, doc, docx)
    public Map uploadFile(MultipartFile file, String publicId) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "raw"
            );

            return cloudinary.uploader().upload(file.getBytes(), options);

        } catch (IOException e) {
            throw new RuntimeException("Upload file failed", e);
        }
    }

    public List<FileAttachment> findByTaskId(Long taskId) {
        return fileAttachmentRepository.findByTaskId(taskId);
    }

    public FileAttachment findById(Long fileAttachmentId) {
        return fileAttachmentRepository.findById(fileAttachmentId).orElseThrow(() -> new RuntimeException("File not found"));
    }

}
