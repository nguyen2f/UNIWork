package com.uniwork.model.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private Long recipientId;
    private Long senderId;

    private String entityType; // e.g., "Project", "Task"
    private Long entityId; // ID of the project or task

    private String type;
    private String title;
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false; // Indicates if the notification has been read
    private LocalDateTime createdDate; // Timestamp when the notification was created
}
