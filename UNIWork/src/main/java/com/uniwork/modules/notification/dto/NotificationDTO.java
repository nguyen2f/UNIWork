package com.uniwork.modules.notification.dto;

import com.uniwork.enums.NotificationEntityType;
import com.uniwork.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private NotificationType type;
    private NotificationEntityType entityType;
    private Long entityId;
    private String title;
    private LocalDateTime createdAt;
}
