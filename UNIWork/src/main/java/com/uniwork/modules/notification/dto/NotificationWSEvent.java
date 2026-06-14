package com.uniwork.modules.notification.dto;

import com.uniwork.enums.NotificationEntityType;
import com.uniwork.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationWSEvent {
    private Long notiId;            // ← QUAN TRỌNG: FE cần để markAsRead
    private Long recipientId;
    private NotificationEntityType entityType;
    private Long entityId;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdDate;
}

