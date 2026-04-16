package com.uniwork.modules.file.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileAttachmentRequest {
    private Long taskId;
    private Long uploaderId;
    private String fileName;
    private String url;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private MultipartFile file;
}
