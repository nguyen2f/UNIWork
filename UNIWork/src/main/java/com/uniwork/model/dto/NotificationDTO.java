package com.uniwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long notiId;

    private Long recipientId;
    private Long senderId;

    private String entityType;
    private Long entityId;

    private String type;
    private String title;
    private String message;
    private LocalDateTime createdDate;
}
