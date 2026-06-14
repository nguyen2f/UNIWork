package com.uniwork.modules.notification.service;

import com.uniwork.modules.notification.dto.NotificationDTO;
import com.uniwork.modules.notification.dto.NotificationWSEvent;
import com.uniwork.modules.notification.entity.Notification;
import com.uniwork.modules.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotiIdAndRecipientId(notificationId, userId);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void markAsReadAll(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    public List<Notification> getAllNotificationByUserId(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedDateDesc(userId);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByNotiIdAndRecipientId(notificationId, userId);
        if (notification != null) {
            notification.setIsDeleted(true);
            notificationRepository.save(notification);
        }
    }

    @Async
    public void sendNotification(Long recipientId, NotificationDTO dto) {

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .entityType(dto.getEntityType())
                .entityId(dto.getEntityId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getType().getDefaultMessage())
                .isRead(false)
                .isDeleted(false)
                .createdDate(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        NotificationWSEvent wsEvent = NotificationWSEvent.builder()
            .notiId(notification.getNotiId())       // ← CẦN THIẾT cho markAsRead
            .recipientId(recipientId)
            .entityType(notification.getEntityType())
            .entityId(notification.getEntityId())
            .type(notification.getType())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .read(false)
            .createdDate(notification.getCreatedDate())
            .build();

        messagingTemplate.convertAndSendToUser(
            recipientId.toString(),   // principal name
            "/queue/notifications",   // user-specific queue
            wsEvent
        );
    }

}
