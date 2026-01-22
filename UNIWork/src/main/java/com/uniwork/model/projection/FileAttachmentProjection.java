package com.uniwork.model.projection;

import java.time.LocalDateTime;

public interface FileAttachmentProjection {
    Long getFileId();
    Long getUploaderId();
    String getUploaderName();
    String getPublicId();
    String getOriginalFileName();
    String getFileUrl();
    String getFileType();
    String getContentType();
    Long getFileSize();
    LocalDateTime getUploadDate();
}