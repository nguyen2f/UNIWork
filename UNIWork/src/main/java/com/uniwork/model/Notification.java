package com.uniwork.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    private Long senderId;
    private String entityType; // e.g., "Project", "Task"
    private Long entityId; // ID of the project or task
    private String title;
    private String message;
    private boolean isRead; // Indicates if the notification has been read
    private Date createdDate; // Timestamp when the notification was created
}
