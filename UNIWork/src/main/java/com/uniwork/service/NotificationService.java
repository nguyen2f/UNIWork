package com.uniwork.service;

import com.uniwork.model.dto.NotificationDTO;
import com.uniwork.model.entity.Notification;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface NotificationService {

    @Async
    void sendNotification(Long recipientId, NotificationDTO dto);

    List<Notification> getAllNotificationByUserId(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAsReadAll(Long userId);
}
