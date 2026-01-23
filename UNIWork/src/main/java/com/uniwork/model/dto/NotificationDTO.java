package com.uniwork.model.dto;

import com.uniwork.model.enumuration.NotificationEntityType;
import com.uniwork.model.enumuration.NotificationType;
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
    private LocalDateTime createdAt;
}
