package com.uniwork.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long taskId; // ID of the task this file is attached to
    private Long uploaderId; // ID of the user who uploaded the file
    private String fileName; // Name of the file
    private String url; // URL where the file is stored
    private String fileType; // Type of the file (e.g., image, document)
    private Long fileSize;
    private Date uploadDate; // Date when the file was uploaded
}
