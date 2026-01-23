package com.uniwork.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "file_attachments")
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private Long taskId; // ID of the task this file is attached to
    private Long uploaderId; // ID of the user who uploaded the file
    private String publicId; // Public ID from cloud storage
    private String originalFileName; // Name of the file
    private String fileUrl; // URL where the file is stored
    private String fileType; // Type of the file (e.g., image, document)
    private String contentType; // application/pdf, image/png
    private Long fileSize;
    private LocalDateTime uploadDate; // Date when the file was uploaded
}
