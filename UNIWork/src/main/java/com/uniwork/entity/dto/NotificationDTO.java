package com.uniwork.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long notiId;

    private Long recipientId;
    private Long senderId;

    private String entityType; // e.g., "Project", "Task"
    private Long entityId; // ID of the project or task

    private String type;
    private String title;
    private String message;
    private Date createdDate; // Timestamp when the notification was created

}
