package com.uniwork.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileAttachmentRequest {
    private Long taskId; // ID of the task this file is attached to
    private Long uploaderId; // ID of the user who uploaded the file
    private String fileName; // Name of the file
    private String url; // URL where the file is stored
    private String fileType; // Type of the file (e.g., image, document)
    private Long fileSize;
    private LocalDateTime uploadDate; // Date when the file was uploaded
}
