package com.uniwork.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
