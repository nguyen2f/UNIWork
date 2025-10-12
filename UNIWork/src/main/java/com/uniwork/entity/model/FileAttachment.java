package com.uniwork.entity.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "file_attachments")
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private Long taskId; // ID of the task this file is attached to
    private Long uploaderId; // ID of the user who uploaded the file
    private String fileName; // Name of the file
    private String url; // URL where the file is stored
    private String fileType; // Type of the file (e.g., image, document)
    private Long fileSize;
    private LocalDateTime uploadDate; // Date when the file was uploaded
}
