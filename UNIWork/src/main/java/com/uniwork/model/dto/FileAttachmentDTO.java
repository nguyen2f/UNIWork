package com.uniwork.model.dto;

import com.uniwork.model.projection.FileAttachmentProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileAttachmentDTO {
    private Long fileId;
    private Long uploaderId; // ID of the user who uploaded the
    private String uploaderName; // Name of the user who uploaded the file
    private String publicId; // Public ID from cloud storage
    private String originalFileName; // Name of the file
    private String fileUrl; // URL where the file is stored
    private String fileType; // Type of the file (e.g., image, document)
    private String contentType; // application/pdf, image/png
    private Long fileSize;
    private LocalDateTime uploadDate; // Date when the file was uploaded

    public FileAttachmentDTO(FileAttachmentProjection projection) {
        this.fileId = projection.getFileId();
        this.uploaderId = projection.getUploaderId();
        this.uploaderName = projection.getUploaderName();
        this.publicId = projection.getPublicId();
        this.originalFileName = projection.getOriginalFileName();
        this.fileUrl = projection.getFileUrl();
        this.fileType = projection.getFileType();
        this.contentType = projection.getContentType();
        this.fileSize = projection.getFileSize();
        this.uploadDate = projection.getUploadDate();
    }
}
